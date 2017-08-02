#import <stdio.h>
#import "includes/silcincludes.h"
#import "silcclient.h"
#import "UserList.h"
#import "ChatController.h"
#import "ClientManager.h"

@implementation ChatController

- (IBAction)sendCommand:(id)sender
{
	NSString* message = [inputField stringValue];
	if ([message characterAtIndex:0] == '/')
	{
		NSString* result = [self parseAndDoCommand:message];
		if (result != nil)
			[self displayMessage:result];
	}
	else if (conn && channel)
	{
		silc_client_send_channel_message(client, conn, channel, NULL, 0, [message cString], [message length], FALSE);
		[self displayMessage:[NSString stringWithFormat:@"<%s> %@", client->nickname, message]];
	}
	[inputField setStringValue:@""];
	[inputField selectText:nil];
}

- (void)sendRealCommand:(NSString*)command
{
	silc_client_command_call(client, conn, [command cString]);
}

- (NSString*)parseAndDoCommand:(NSString*)message
{
	NSArray* tokens = [message componentsSeparatedByString:@" "];
	if (conn)
	{
		if ([[tokens objectAtIndex:0] isEqual:@"/me"])
		{
			NSString* action = @"";
			int i;
			for (i=1; i<[tokens count]; i++)
			{
				action = [action stringByAppendingString:[tokens objectAtIndex:i]];
				action = [action stringByAppendingString:@" "];
			}
			silc_client_send_channel_message(client, conn, channel, NULL, SILC_MESSAGE_FLAG_ACTION, [action cString], [action length], FALSE);
			return [NSString stringWithFormat:@"* %s %@", client->username, action];
		}
		if ([[tokens objectAtIndex:0] isEqual:@"/join"])
		{
			if (channel)
				[self sendRealCommand:[NSString stringWithFormat:@"LEAVE %s", channel->channel_name]];
			[self sendRealCommand:[NSString stringWithFormat:@"JOIN %@", [tokens objectAtIndex:1]]];
		}
		else if ([[tokens objectAtIndex:0] isEqual:@"/users"])
		{
			if (channel)
				[self sendRealCommand:[NSString stringWithFormat:@"USERS %s", channel->channel_name]];
		}
		else if ([[tokens objectAtIndex:0] isEqual:@"/op"])
		{
			if (channel)
				[self sendRealCommand:[NSString stringWithFormat:@"CUMODE %s +o %@", channel->channel_name, [tokens objectAtIndex:1]]];
		}
		else if ([[tokens objectAtIndex:0] isEqual:@"/cumode"])
		{
			if (channel)
				[self sendRealCommand:[NSString stringWithFormat:@"CUMODE %@ %@ %@", [tokens objectAtIndex:1], [tokens objectAtIndex:2], [tokens objectAtIndex:3]]];
		}
		else if ([[tokens objectAtIndex:0] isEqual:@"/nick"])
			[self sendRealCommand:[NSString stringWithFormat:@"NICK %@", [tokens objectAtIndex:1]]];
		else if ([[tokens objectAtIndex:0] isEqual:@"/whois"])
			[self sendRealCommand:[NSString stringWithFormat:@"WHOIS %@", [tokens objectAtIndex:1]]];
		else if ([[tokens objectAtIndex:0] isEqual:@"/quote"])
		{
			NSString* command = @"";
			int i;
			for (i=1; i<[tokens count]; i++)
			{
				command = [command stringByAppendingString:[tokens objectAtIndex:i]];
				command = [command stringByAppendingString:@" "];
			}
			[self sendRealCommand:command];
		}
	}
	else
	{
		if ([[tokens objectAtIndex:0] isEqual:@"/server"] || [[tokens objectAtIndex:0] isEqual:@"/connect"])
		{
			if ([tokens count] > 2)
				[self startConnectionToServer:[tokens objectAtIndex:1] withPort:[[tokens objectAtIndex:2] intValue]];
			else
				[self startConnectionToServer:[tokens objectAtIndex:1] withPort:706];
		}
	}
	return nil;
}

- (void)displayMessage:(NSString*)message
{
	NSRange endOfChat = {[[chatField string] length], 0};
	[chatField setSelectedRange:endOfChat];
	[chatField setEditable:YES];
	NSDate* time = [NSDate date];
	NSString* timestamp = [time descriptionWithCalendarFormat:@"[%H:%M:%S] " timeZone:nil locale:nil];
	[chatField insertText:timestamp];
	[chatField insertText:message];
	[chatField insertText:@"\n"];
	[chatField setEditable:NO];
	logPath = [[NSBundle mainBundle] bundlePath];
	logPath = [logPath stringByAppendingPathComponent:@"Contents/Logs/silc.log"];
	FILE* log = fopen([logPath cString], "a");
	fprintf(log, "%s%s\n", [timestamp cString], [message cString]);
	fclose(log);
}

- (void)fillUserList:(UserList*)list
{
	//silc_client_command_call(client, conn, "USERS thington");
	[userList setDataSource:list];
	[userList reloadData];
}

- (void)awakeFromNib
{
	[userList sizeToFit];
	[[chatField window] makeKeyAndOrderFront:nil];
	[[ClientManager instance] add:self];
	
	logPath = [[NSBundle mainBundle] bundlePath];
	logPath = [logPath stringByAppendingPathComponent:@"Contents/Logs/silc.log"];
	FILE* log = fopen([logPath cString], "a");
	fprintf(log, "\n\nStarting logging on %s:\n", [[[NSDate date] description] cString]);
	fclose(log);
	
	SilcClientParams params = {0, 0, 0, "%n@%h%a", FALSE, silc_parse_nickname, FALSE, FALSE};
	// See mybot.c for comments on this section, copied almost directly
	client = silc_client_alloc(&opers, &params, self, NULL);
	if (!client) {
		NSLog(@"Could not allocate SILC Client");
		exit(1);
	}
	
	client->nickname = silc_get_username();
	client->username = silc_get_username();
	client->hostname = silc_net_localhost();
	client->realname = strdup("SILC Aqua Dev");
	
	if (!silc_client_init(client)) {
		NSLog(@"Could not init client");
		exit(1);
	}
	
	NSString* appPath = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:@"Contents/Keys"];
	NSString* pubKeyPath = [appPath stringByAppendingPathComponent:@"silcaqua.pub"];
	NSString* prvKeyPath = [appPath stringByAppendingPathComponent:@"silcaqua.prv"];
	if (!silc_load_key_pair([pubKeyPath cString], [prvKeyPath cString], "", &client->pkcs, &client->public_key, &client->private_key)) {
		[self displayMessage:@"Key pair does not exist; generating it."];
	if (!silc_create_key_pair("rsa", 2048, [pubKeyPath cString], [prvKeyPath cString], NULL, "",
											&client->pkcs, &client->public_key, &client->private_key, FALSE)) {
			NSLog(@"Could not generate key pair");
			exit(1);
		}
	}
}

- (void)startConnectionToServer:(NSString*)server withPort:(int)port
{
	silc_client_connect_to_server(client, NULL, port, [server cString], self);
	runTimer = [NSTimer scheduledTimerWithTimeInterval:0.05 target:self selector:@selector(runOnce) userInfo:NULL repeats:YES];
}

- (void)runOnce
{
	silc_client_run_one(client);
}

- (void)setChannel:(SilcChannelEntry)chan
{
	channel = chan;
}

- (void)setConnection:(SilcClientConnection)connect
{
	conn = connect;
}

- (SilcClient)getClient
{
	return client;
}

- (void)windowWillClose:(NSNotification*)note;
{
	[runTimer invalidate];
	silc_client_close_connection(client, conn);
	silc_client_stop(client);
	silc_client_free(client);
	NSLog(@"Closing window.");
}

@end