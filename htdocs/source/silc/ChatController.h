/* ChatController */

#import <Cocoa/Cocoa.h>

@interface ChatController : NSObject
{
    IBOutlet id chatField;
    IBOutlet id inputField;
    IBOutlet id userList;
	NSTimer* runTimer;
	SilcClient client;
	SilcClientConnection conn;
	SilcChannelEntry channel;
	NSString* logPath;
}
- (IBAction)sendCommand:(id)sender;
- (void)sendRealCommand:(NSString*)command;
- (void)displayMessage:(NSString*)message;
- (void)fillUserList:(UserList*)list;
- (void)awakeFromNib;
- (void)startConnectionToServer:(NSString*)server withPort:(int)port;
- (void)runOnce;
- (void)setChannel:(SilcChannelEntry)chan;
- (void)setConnection:(SilcClientConnection)connect;
- (SilcClient)getClient;
- (void)windowWillClose:(NSNotification*)note;
@end
