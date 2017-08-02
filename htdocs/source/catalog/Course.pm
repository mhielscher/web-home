package Course;
sub new {
	my ($class, $number, $title, $units, $instructor, $prereqs, $recs, $desc) = @_;
	my $self = {number => $number,
				title => $title,
				units => $units,
				instructor => $instructor,
				prereqs => $prereqs,
				recs => $recs,
				desc => $desc};
	bless($self, $class);
	return $self;
}

sub print {
	my ($self) = @_;
	print "$self->{number}. $self->{title}\n";
	print "($self->{units}) $self->{instructor}\n";
	print "$self->{prereqs}\n";
	print "$self->{recs}\n" if ($self->{'recs'});
	print "$self->{desc}\n\n";
}

sub fileoutput {
	my ($self) = @_;
	print "$self->{'number'}@";
	print "$self->{'title'}@";
	print "$self->{'units'}@";
	print "$self->{'instructor'}@";
	print "$self->{'prereqs'}@";
	print "$self->{'recs'}@";
	print "$self->{'desc'}@\n";
}

return true;