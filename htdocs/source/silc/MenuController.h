/* MenuController */

#import <Cocoa/Cocoa.h>

@interface MenuController : NSObject
{
    IBOutlet id aboutWindow;
    IBOutlet id mainWindow;
    IBOutlet id prefsWindow;
}
- (IBAction)aboutWindow:(id)sender;
- (IBAction)closeWindow:(id)sender;
- (IBAction)newConnection:(id)sender;
- (IBAction)newWindow:(id)sender;
- (IBAction)openPreferences:(id)sender;

- (void)awakeFromNib;
@end
