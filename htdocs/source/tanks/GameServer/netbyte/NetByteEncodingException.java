// saleem, Oct 2002
// saleem, Dec 2001
// for use with NetByte.java

package netbyte;

/**
To indicate problems during encoding.

<br />
<b>Author:</b> Saleem N. Bhatti &lt;s.bhatti@cs.ucl.ac.uk&gt;<br />
<b>Version:</b> 1.1<br />
<b>Date:</b> Oct 2002<br />

<p>
Generated during the encoding of a value into a byte array passed
to the method.
</p>

*/


public class NetByteEncodingException extends NetByteException
{
    /**
       @param msg message to display when Exception.getMessage() is called.
    */
    NetByteEncodingException(String message)
    { super(message); }
}
