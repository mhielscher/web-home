// saleem, Oct 2002
// saleem, Dec 2001
// for use with NetByte.java

package netbyte;

/**
To indicate problems using NetByte.

<br />
<b>Author:</b> Saleem N. Bhatti &lt;s.bhatti@cs.ucl.ac.uk&gt;<br />
<b>Version:</b> 1.1<br />
<b>Date:</b> Oct 2002<br />

<p>
This class is the Exception class from which all other NetByte exceptions
are derived.
</p>

*/

public class NetByteException extends Exception
{
    /**
       @param msg message to display when Exception.getMessage() is called.
    */
    NetByteException(String message)
    { super(message); }
}
