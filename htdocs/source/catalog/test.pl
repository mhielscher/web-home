#!/usr/bin/perl

$line = 'consent of instructor (for 121A): ECE 121A or Physics 121A; consent of instructor (for 121B). ';
print "$line\n";
$classcode = '[\w\s]*?\d{1,3}(?:\w(?:\w-\w\w)?)?';
$op = '(?:;?(?:\||&))|(?:; )';
$line =~ s/<.*>//g;
$line =~ s/\(.*?\)//g;
#$line =~ s/ ((each )?with a minimum grade of [ABCD][+-]?( in (both|each( course)?|all|either|any))?)|(with a grade of [ABCD][+-]? or better)|(with minimum grades of [ABCD][+-]?)//g;
$line =~ s/\. /; /g;
$line =~ s/; ((and)|(or)),? /\) $1 \(/g;
$line =~ s/; /\) and \(/g;
$line =~ s/(\d{1,3})(\w)-(\w)-(\w)/$1$2 and $1$3 and $1$4/g;
$line =~ s/(\d{1,3})(\w)-(\w)/$1$2 and $1$3/g;
$line =~ s/($classcode), ($classcode)\)/$1 and $2)/g;
print "$line\n";
#to reduce long strings of comma-joined classes, in reverse
while ($line =~ s/(\d{1,3}(\w\w?)?), (\d{1,3}(\w\w?)?),? ((and)|(or))/$1 $5 $3 $5/) {}
$line =~ s/ and,? /&/g;
$line =~ s/ or,? /|/g;
#$line =~ s/ \(.*?may be taken concurrently\)//g;
$line = "(($line))";
$line =~ s/(([Ll]ecture)|([Ll]ab(oratory)?)|([Dd]iscussion)|([Ss]eminar)).*$//;
print "$line\n";
my $final = "";
while ($line =~ m/(\(*).*?($classcode)(?:.*?)((\)+$op)|(\)+)|($op))/g) {
	$final .= "$1$2$3";
}
$final =~ s/($op)$//;
$final .= ")" unless ($final eq "");
print "$final\n";
#([classcode])[junk]?(([operator])[junk]?([classcode])[junk]?)*
#(*[junk][classcode][junk])*[op](*[junk][classcode][junk])*
