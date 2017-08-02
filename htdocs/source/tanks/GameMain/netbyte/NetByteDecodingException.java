// saleem, Oct 2002
// saleem, Dec 2001
// for use with NetByte.java

package netbyte;

/**
To indicate problems during decoding.

<br />
<b>Author:</b> Saleem N. Bhatti &lt;s.bhatti@cs.ucl.ac.uk&gt;<br />
<b>Version:</b> 1.1<br />
<b>Date:</b> Oct 2002<br />

<p>
Generated during the decoding of a byte array.
</p>

*/

public class NetByteDecodingException extends NetByteException
{
    /**
       @param msg message to display when Exception.getMessage() is called.
    */
    NetByteDecodingException(String message)
    { super(message); }
}
