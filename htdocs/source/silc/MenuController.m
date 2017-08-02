#import "MenuController.h"

@implementation MenuController

- (IBAction)aboutWindow:(id)sender
{
}

- (IBAction)closeWindow:(id)sender
{
}

- (IBAction)newConnection:(id)sender
{
}

- (IBAction)newWindow:(id)sender
{
//	if (mainWindow == nil)
//	{
		if (![NSBundle loadNibNamed:@"MainWindow.nib" owner:self])
		{
			NSLog(@"Load of MainWindow.nib failed");
			return;
		}
//	}
	[mainWindow makeKeyAndOrderFront:nil];
}

- (IBAction)openPreferences:(id)sender
{
}

- (void)awakeFromNib
{
	//[mainWindow makeKeyAndOrderFront:nil];
}

@end
