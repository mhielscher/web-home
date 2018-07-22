$pr =~ s/<.*>//g;
$pr =~ s/; ((and)|(or)),? /\) $1 \(/g;
$pr =~ s/; /\) and \(/g;
$pr =~ s/(\d{1,3})(\w)-(\w)-(\w)/$1$2 and $1$3 and $1$4/g;
$pr =~ s/(\d{1,3})(\w)-(\w)/$1$2 and $1$3/g;
$pr =~ s/(\d{1,3}(\w\w?)?), (\d{1,3}(\w\w?)?),? ((and)|(or))/$1 $5 $3 $5/g;
$pr =~ s/ and,? /&/g;
$pr =~ s/ or,? /|/g;
$pr =~ s/ \(.*?may be taken concurrently\)//g;
$pr =~ s/ ((each )?with a minimum grade of [ABCD][+-]?( in (both|all|either|any))?)|(with a grade of [ABCD][+-]? or better)|(with minimum grades of [ABCD][+-]?)//g;
$pr = "(($pr))";

%precedence = ( '(' => 1, ')' => 1, '&' => 2, '|' => 2);
my $postfix = "";
my @opstack = ();
while ($pr =~ m/^([()&|]|([^()&|]*))/) {
	my $symbol = $1;
	$pr =~ s/$symbol//;
	if (!defined($precedence{$symbol})) { #symbol is not an operator
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

# ((ME 104&163)|(ECE 130A&137A)&())
# ME 104 and 163; or, ECE 130A and 137A; with a minimum grade of C- in both.

#format: term =~ /\([^)]\)/

#read the first topmost term (figure out how to find that text after repeated
#matches of ()), add it to the tree (reference? symbolically? indexes?), then
#check to see if the first character is an op or a term. If it's a term, ...

#maybe just do the damn processing in-order:
#symbols: (, ), &, |, [^()&|]+
#read first symbol (m/^([()&|]|([^()&|]*))/)
#if not an operator, add to postfix string
#elsif operator eq '('
# push it onto the stack
#elsif operator eq ')'
# while stack.top ne '('
#  pop stack, push it onto postfix string
#else,
# while stack !empty && stack.top.precedence > operator.precedence,
#  pop stack, push it onto postfix string
# push operator onto stack



my ($pr) = @_;
#my $match = "(";
#for $key (keys %rephash) {
#	$match .= "$key|";
#}
#chop($match);
#$match .= ")";
#$pr =~ s/$match/$rephash{$1}/ge;


my @prereqList = ();
my @logAnds = ();
$pr =~ s/<.*>//g;
$pr =~ s/; and,? /|/g;
$pr =~ s/\. /; /g;
$pr =~ s/; /|/g;
$pr =~ s/(\d{1,3})(\w)-(\w)-(\w)/$1$2 and $1$3 and $1$4/g;
$pr =~ s/(\d{1,3})(\w)-(\w)/$1$2 and $1$3/g;
$pr =~ s/(\d{1,3}(\w\w?)?), (\d{1,3}(\w\w?)?),? and/$1 and $3 and/g;
$pr =~ s/ and /|/g;
$pr =~ s/ or, / or /g;
$pr =~ s/ \(.*?may be taken concurrently\)//g;
$pr =~ s/ ((each )?with a minimum grade of [ABCD][+-]?( in (both|all|either|any))?)|(with a grade of [ABCD][+-]? or better)|(with minimum grades of [ABCD][+-]?)//g;
#print "FP: $pr\n";
@logAnds = split(/\|/, $pr);
my $department = "*null*";
for $term (@logAnds) {
	if ($term =~ /([Oo]pen to)|([Cc]onsent)|([Ll]ecture)|([Ll]aboratory)/) {
		last;
	}
	my @logOrs = split(/ or /, $term);
	for $class (@logOrs) {
		$class =~ s/[.,\s]*$//g;
		if ($class =~ /^\d{1,3}(\w\w?)?/) {
			$class = "$department $class";
		}
		else {
			$class =~ /(.*) \d{1,3}(\w\w?)?/;
			$department = $1;
		}
	}
	push(@prereqList, \@logOrs);
}

return \@prereqList;

my $printPR = "";
my @prereqs = @{$self->{'prereqs'}};
if ($#prereqs != -1) {
	print "Prereqs: (";
	for $term (@prereqs) {
		for $interm (@$term) {
			#print "$interm";
			$printPR .= "$interm + ";
		}
		$printPR = substr($printPR, 0, -3);
		$printPR .= ")(";
	}
	$printPR = substr($printPR, 0, -1);
	print "$printPR\n";
}


my @prereqs = @{$self->{'prereqs'}};
for $term (@prereqs) {
	for $interm (@$term) {
		print "$interm!";
	}
	print "#";
}




$pr =~ s/<.*>//g;
$pr =~ s/ ((each )?with a minimum grade of [ABCD][+-]?( in (both|each( course)?|all|either|any))?)|(with a grade of [ABCD][+-]? or better)|(with minimum grades of [ABCD][+-]?)//g;
$pr =~ s/\. /; /g;
$pr =~ s/; ((and)|(or)),? /\) $1 \(/g;
$pr =~ s/; /\) and \(/g;
$pr =~ s/(\d{1,3})(\w)-(\w)-(\w)/$1$2 and $1$3 and $1$4/g;
$pr =~ s/(\d{1,3})(\w)-(\w)/$1$2 and $1$3/g;
$pr =~ s/(\d{1,3}(\w\w?)?), (\d{1,3}(\w\w?)?),? ((and)|(or))/$1 $5 $3 $5/g;
$pr =~ s/ and,? /&/g;
$pr =~ s/ or,? /|/g;
$pr =~ s/ \(.*?may be taken concurrently\)//g;
$pr = "(($pr))";
$pr =~ s/\([^()]*(([Oo]pen to)|([Cc]onsent)|([Ll]ecture)|([Ll]aboratory)|([Dd]iscussion)|([Ss]eminar)|([Mm]ajors)|([Hh]ours))[^()]*\)/\(\)/g;
$pr =~ s/[&|]\(\)//g;