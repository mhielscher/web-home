#!/usr/bin/perl

package main;
use warnings;

#%rephash = ( "CS" => "Computer Science",
#				"ChemE" => "ChemE",
#				"PSTAT" => "PSTAT",
#				"ECE" => "ECE" );

sub parsePrereqs {
	my ($pr) = @_;
	print "$pr\n";
	$classcode = '[\w\s]*?\d{1,3}(?:\w(?:\w-\w\w)?)?';
	$op = '(?:;?(?:\||&))|(?:; )';
	$pr =~ s/<.*>//g;
	$pr =~ s/\(.*?\)//g;
	#$pr =~ s/ ((each )?with a minimum grade of [ABCD][+-]?( in (both|each( course)?|all|either|any))?)|(with a grade of [ABCD][+-]? or better)|(with minimum grades of [ABCD][+-]?)//g;
	$pr =~ s/\. /; /g;
	$pr =~ s/; ((and)|(or)),? /\) $1 \(/g;
	$pr =~ s/; /\) and \(/g;
	$pr =~ s/(\d{1,3})(\w)-(\w)-(\w)/$1$2 and $1$3 and $1$4/g;
	$pr =~ s/(\d{1,3})(\w)-(\w)/$1$2 and $1$3/g;
	$pr =~ s/($classcode), (?!and )(?!or )($classcode)\)/$1 and $2)/g;
	print "$pr\n";
	#to reduce long strings of comma-joined classes, in reverse
	while ($pr =~ s/(\d{1,3}(\w\w?)?), (\d{1,3}(\w\w?)?),? ((and)|(or))/$1 $5 $3 $5/) {}
	$pr =~ s/ and,? /&/g;
	$pr =~ s/ or,? /|/g;
	#$pr =~ s/ \(.*?may be taken concurrently\)//g;
	$pr = "(($pr))";
	$pr =~ s/(([Ll]ecture)|([Ll]ab(oratory)?)|([Dd]iscussion)|([Ss]eminar)).*$//;
	print "$pr\n";
	my $final = "";
	while ($pr =~ m/(\(*).*?($classcode)(?:.*?)((\)+$op)|(\)+)|($op))/g) {
		$final .= "$1$2$3";
	}
	$final =~ s/($op)$//;
	$final .= ")" unless ($final eq "");
	print "$final\n\n";

	%precedence = ( '(' => 1, ')' => 1, '&' => 2, '|' => 2);
	my $postfix = "";
	my $lastclass = "";
	my @opstack = ();
	while ($final =~ m/^([()&|]|([^()&|]+))/) {
		my $symbol = $1;
		$final =~ s/\Q$symbol//;
		$symbol =~ s/^\s*//;
		$symbol =~ s/\.?\s*$//;
		if (!defined($precedence{$symbol})) { #symbol is not an operator
			if ($symbol =~ /^\d{1,3}(\w\w?)?/) { #number with no department
				$lastclass =~ /(.*) \d{1,3}(\w\w?)?/;
				$symbol = "$1 $symbol";
			}
			$lastclass = $symbol;
			$postfix .= "$symbol!"; #! is the term delimeter, because terms can have \s
		}
		elsif ($symbol eq '(') {
			push(@opstack, $symbol);
		}
		elsif ($symbol eq ')') {
			while ($opstack[-1] ne '(') {
				$postfix .= pop(@opstack);
			}
		}
		else {
			while ($#opstack > -1 && $precedence{$opstack[-1]} > $precedence{$symbol}) {
				$postfix .= pop(@opstack);
			}
			push(@opstack, $symbol);
		}
	}
	while ($#opstack > -1) {
		$operator = pop(@opstack);
		$postfix .= $operator unless ($operator eq '(');
	}
	
	return $postfix;
}

use Course;
my $line;
#while (<STDIN> !~ /courses-division-hed/) {}
my @courses = ();
my $currCourse;
while ($line = <STDIN>) {
	if ($line =~ /course-hed-title/) {
		$line =~ />([\d\w\-]+)\. (.+)</;
		$currCourse = Course->new($1, $2);
		push(@courses, $currCourse); #it's a reference, so this should work fine
	}
	elsif ($line =~ /course-instructor/) {
		$line =~ />\((\d\d?(-\d\d?)?)\) (.+)</;
		$currCourse->{"units"} = $1;
		$currCourse->{"instructor"} = $3;
		#$currCourse->print();
	}
	elsif ($line =~ /course-prerequisite/) {
		if ($line !~ /Recommended preparation:/) {
			$line =~ />Prerequisites?: (.+)</;
			#print "$1\n";
			$currCourse->{"prereqs"} = parsePrereqs($1);
		}
	}
	elsif ($line =~ /course-enroll-rec/) {
		$line =~ />(.+)</;
		$currCourse->{"recs"} = $1;
	}
	elsif ($line =~ /course-text/) {
		$line =~ />(.+)</;
		$currCourse->{"desc"} .= "\n" if ($currCourse->{"desc"} ne "");
		$currCourse->{"desc"} .= $1;
	}
}

for $course (@courses) {
	$course->print();
}