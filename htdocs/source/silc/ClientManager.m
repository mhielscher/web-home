//
//  ClientManager.m
//  SILC Aqua
//
//  Created by Matthew Hielscher on 3/22/05.
//  Copyright 2005 __MyCompanyName__. All rights reserved.
//

#import "includes/silcincludes.h"
#import "silcclient.h"
#import "UserList.h"
#import "ChatController.h"
#import "ClientManager.h"


@implementation ClientManager

+ (ClientManager*)instance
{
	if (theManager == nil)
		theManager = [[ClientManager alloc] init];
	return theManager;
}

- (id)init
{	
	if (![super init])
		return nil;
	clientList = [[NSMutableArray alloc] initWithCapacity:1];
	//theManager = self;
	return self;
}

- (ChatController*)getController:(SilcClient)client
{
	if (!clientList)
	{
		NSLog(@"clientList should not be nil, but it is...");
		return nil;
	}
	unsigned int size = [clientList count];
	int i;
	ChatController* controller;
	for (i=0; i<size; i++)
	{
		controller = [clientList objectAtIndex:i];
		if (controller != nil)
			if ([controller getClient] == client)
				return controller;
	}
	return nil;
}

- (void)add:(ChatController*)controller
{
	[clientList addObject:controller];
}

- (ChatController*)remove:(ChatController*)controller
{
	[clientList removeObject:controller];
	return controller;
}

- (ChatController*)removeByClient:(SilcClient)client
{
	ChatController* result = [self getController:client];
	if (result != nil)
	{
		[self remove:result];
		return result;
	}
	return nil;
}

@end


/******* SILC Client Operations **********************************************/

/* The SILC Client Library requires these "client operations".  They are
   functions that the library may call at any time to indicate to application
   that something happened, like message was received, or authentication
   is required or something else.  Since our MyBot is really simple client
   we don't need most of the operations, so we just define them and don't
   do anything in them. */

/* "say" client operation is a message from the client library to the
   application.  It may include error messages or something else.  We
   just dump them to screen. */

static void
silc_say(SilcClient client, SilcClientConnection conn,
	 SilcClientMessageType type, char *msg, ...)
{
  char str[200];
  va_list va;
  va_start(va, msg);
  vsnprintf(str, sizeof(str) - 1, msg, va);
  //fprintf(stdout, "MyBot: %s\n", str);
  ChatController* thisChat = [[ClientManager instance] getController:client];
  [thisChat displayMessage:[NSString stringWithCString:str]];
  va_end(va);
}


/* Message for a channel. The `sender' is the sender of the message
   The `channel' is the channel. The `message' is the message.  Note
   that `message' maybe NULL.  The `flags' indicates message flags
   and it is used to determine how the message can be interpreted
   (like it may tell the message is multimedia message). */

static void
silc_channel_message(SilcClient client, SilcClientConnection conn,
		     SilcClientEntry sender, SilcChannelEntry channel,
		     SilcMessagePayload payload, SilcChannelPrivateKey key,
		     SilcMessageFlags flags, const unsigned char *message,
		     SilcUInt32 message_len)
{
	/* Yay! We got a message from channel. */
	if (!message)
		return;
	ChatController* thisChat = [[ClientManager instance] getController:client];
	if (flags & SILC_MESSAGE_FLAG_SIGNED)
		[thisChat displayMessage:[NSString stringWithFormat:@"[SIGNED] <%s> %s", sender->nickname, message]];
	else if (flags & SILC_MESSAGE_FLAG_ACTION)
		[thisChat displayMessage:[NSString stringWithFormat:@"* %s %s", sender->nickname, message]];
	else
		[thisChat displayMessage:[NSString stringWithFormat:@"<%s> %s", sender->nickname, message]];
}


/* Private message to the client. The `sender' is the sender of the
   message. The message is `message'and maybe NULL.  The `flags'  
   indicates message flags  and it is used to determine how the message
   can be interpreted (like it may tell the message is multimedia
   message). */

static void
silc_private_message(SilcClient client, SilcClientConnection conn,
		     SilcClientEntry sender, SilcMessagePayload payload,
		     SilcMessageFlags flags,
		     const unsigned char *message,
		     SilcUInt32 message_len)
{
  /* MyBot does not support private message receiving */
}


/* Notify message to the client. The notify arguments are sent in the
   same order as servers sends them. The arguments are same as received
   from the server except for ID's.  If ID is received application receives
   the corresponding entry to the ID. For example, if Client ID is received
   application receives SilcClientEntry.  Also, if the notify type is
   for channel the channel entry is sent to application (even if server
   does not send it because client library gets the channel entry from
   the Channel ID in the packet's header). */

static void
silc_notify(SilcClient client, SilcClientConnection conn,
	    SilcNotifyType type, ...)
{
	char *str;
	va_list va;

	va_start(va, type);
	ChatController* thisChat = [[ClientManager instance] getController:client];
	SilcClientEntry user;
	SilcChannelEntry chan;
	int typeid;
	
	/* Here we can receive all kinds of different data from the server, but
	 our simple bot is interested only in receiving the "not-so-important"
	 stuff, just for fun. :) */
	switch (type) {
		case SILC_NOTIFY_TYPE_NONE:
			/* Received something that we are just going to dump to screen. */
			str = va_arg(va, char *);
			[thisChat displayMessage:[NSString stringWithFormat:@"--- %s", str]];
			break;

		case SILC_NOTIFY_TYPE_MOTD:
			/* Received the Message of the Day from the server. */
			str = va_arg(va, char *);
			//fprintf(stdout, "%s", str);
			[thisChat displayMessage:[NSString stringWithCString:str]];
			break;
		
		case SILC_NOTIFY_TYPE_JOIN:
			user = va_arg(va, SilcClientEntry);
			chan = va_arg(va, SilcChannelEntry);
			[thisChat displayMessage:[NSString stringWithFormat:@"%s has joined this channel.",user->nickname]];
			[thisChat sendRealCommand:[NSString stringWithFormat:@"USERS %s", chan->channel_name]];
			break;
		
		case SILC_NOTIFY_TYPE_LEAVE:
			user = va_arg(va, SilcClientEntry);
			chan = va_arg(va, SilcChannelEntry);
			[thisChat displayMessage:[NSString stringWithFormat:@"%s has left this channel.", user->nickname]];
			[thisChat sendRealCommand:[NSString stringWithFormat:@"USERS %s", chan->channel_name]];
			break;
		
		case SILC_NOTIFY_TYPE_SIGNOFF:
			user = va_arg(va, SilcClientEntry);
			char* message = va_arg(va, char*);
			if (message)
				[thisChat displayMessage:[NSString stringWithFormat:@"%s has quit (%s).", user->nickname, message]];
			else
				[thisChat displayMessage:[NSString stringWithFormat:@"%s has quit.", user->nickname]];
			SilcHashTableList htl;
			silc_hash_table_list(user->channels, &htl);
			SilcChannelEntry context;
			while (silc_hash_table_get(&htl, &context, nil))
			{
				if (context->channel_name)
					[thisChat sendRealCommand:[NSString stringWithFormat:@"USERS %s", context->channel_name]];
			}
			silc_hash_table_list_reset(&htl);
			break;
		
		case SILC_NOTIFY_TYPE_CUMODE_CHANGE:
			typeid = va_arg(va, int);
			char* changername;
			if (typeid == SILC_ID_CLIENT)
			{
				SilcClientEntry temp = va_arg(va, SilcClientEntry);
				changername = temp->nickname;
			}
			else if (typeid == SILC_ID_SERVER)
			{
				SilcServerEntry temp = va_arg(va, SilcServerEntry);
				changername = temp->server_name;
			}
			else if (typeid == SILC_ID_CHANNEL)
			{
				SilcChannelEntry temp = va_arg(va, SilcChannelEntry);
				changername = temp->channel_name;
			}
			SilcUInt32 mode = va_arg(va, SilcUInt32);
			SilcClientEntry target = va_arg(va, SilcClientEntry);
			chan = va_arg(va, SilcChannelEntry);
			//SilcChannelUser chuser;
			//silc_hash_table_find(channel->user_list, target, nil, &chuser);
			//mode = mode ^ chuser->mode;
			if (mode & 0x1)
				[thisChat displayMessage:[NSString stringWithFormat:@"%s gives channel founder status to %s on %s", changername, target->nickname, chan->channel_name]];
			if (mode & 0x2)
				[thisChat displayMessage:[NSString stringWithFormat:@"%s gives channel operator status to %s on %s", changername, target->nickname, chan->channel_name]];
			break;
		
		case SILC_NOTIFY_TYPE_NICK_CHANGE:
			user = va_arg(va, SilcClientEntry);
			SilcClientEntry newuser = va_arg(va, SilcClientEntry);
			[thisChat displayMessage:[NSString stringWithFormat:@"%s is now known as %s", user->nickname, newuser->nickname]];
			break;
		
		case SILC_NOTIFY_TYPE_TOPIC_SET:
			typeid = va_arg(va, int);
			char* name;
			if (typeid == SILC_ID_CLIENT)
			{
				SilcClientEntry temp = va_arg(va, SilcClientEntry);
				name = temp->nickname;
			}
			else if (typeid == SILC_ID_SERVER)
			{
				SilcServerEntry temp = va_arg(va, SilcServerEntry);
				name = temp->server_name;
			}
			else if (typeid == SILC_ID_CHANNEL)
			{
				SilcChannelEntry temp = va_arg(va, SilcChannelEntry);
				name = temp->channel_name;
			}
			char* topic = va_arg(va, char*);
			chan = va_arg(va, SilcChannelEntry);
			[thisChat displayMessage:[NSString stringWithFormat:@"%s has changed the topic of %s to: %s", name, chan->channel_name, topic]];
			break;

		default:
			/* Ignore rest */
			break;
	}

	va_end(va);
}


/* Command handler. This function is called always in the command function.
   If error occurs it will be called as well. `conn' is the associated
   client connection. `cmd_context' is the command context that was
   originally sent to the command. `success' is FALSE if error occurred
   during command. `command' is the command being processed. It must be
   noted that this is not reply from server. This is merely called just
   after application has called the command. Just to tell application
   that the command really was processed. */

static void
silc_command(SilcClient client, SilcClientConnection conn,
	     SilcClientCommandContext cmd_context, bool success,
	     SilcCommand command, SilcStatus status)
{
  /* If error occurred in client library with our command, print the error */
  if (status != SILC_STATUS_OK)
    fprintf(stderr, "COMMAND %s: %s\n",
	    silc_get_command_name(command),
	    silc_get_status_message(status));
}


/* Command reply handler. This function is called always in the command reply
   function. If error occurs it will be called as well. Normal scenario
   is that it will be called after the received command data has been parsed
   and processed. The function is used to pass the received command data to
   the application.

   `conn' is the associated client connection. `cmd_payload' is the command
   payload data received from server and it can be ignored. It is provided
   if the application would like to re-parse the received command data,
   however, it must be noted that the data is parsed already by the library
   thus the payload can be ignored. `success' is FALSE if error occurred.
   In this case arguments are not sent to the application. The `status' is
   the command reply status server returned. The `command' is the command
   reply being processed. The function has variable argument list and each
   command defines the number and type of arguments it passes to the
   application (on error they are not sent). */

static void
silc_command_reply(SilcClient client, SilcClientConnection conn,
		   SilcCommandPayload cmd_payload, bool success,
		   SilcCommand command, SilcStatus status, ...)
{
	va_list va;

	/* If error occurred in client library with our command, print the error */
	if (status != SILC_STATUS_OK)
		fprintf(stderr, "COMMAND REPLY %s: %s\n",
	silc_get_command_name(command),
	silc_get_status_message(status));

	va_start(va, status);

	ChatController* thisChat = [[ClientManager instance] getController:client];
	
	SilcClientEntry user;
	SilcChannelEntry channel;

	switch (command)
	{
		/* Check for successful JOIN */
		case SILC_COMMAND_JOIN:
			(void)va_arg(va, SilcClientEntry);
			channel = va_arg(va, SilcChannelEntry);
			[thisChat setChannel:channel];
			[thisChat displayMessage:[NSString stringWithFormat:@"Joined channel '%s'", channel->channel_name]];
			[thisChat sendRealCommand:[NSString stringWithFormat:@"USERS %s", channel->channel_name]];
			break;

		case SILC_COMMAND_USERS:
			channel = va_arg(va, SilcChannelEntry);
			//va_arg(va, SilcUInt32);
			//SilcBuffer userlist = va_arg(va, SilcBuffer);
			//NSLog(@"%s\n", userlist->head);
			SilcHashTableList htl;
			silc_hash_table_list(channel->user_list, &htl);
			SilcChannelUser context;
			UserList* who = [[UserList alloc] init];
			while (silc_hash_table_get(&htl, nil, &context))
			{
				if (context->client->nickname)
					[who add:[NSString stringWithCString:(context->client->nickname)]];
			}
			[thisChat fillUserList:who];
			silc_hash_table_list_reset(&htl);
			break;
		
		case SILC_COMMAND_NICK:
			va_arg(va, SilcClientEntry);
			char* nick = va_arg(va, char*);
			client->nickname = nick;
			[thisChat displayMessage:[NSString stringWithFormat:@"You are now known as %s", nick]];
			break;
		
		case SILC_COMMAND_WHOIS:
			user = va_arg(va, SilcClientEntry);
			[thisChat displayMessage:[NSString stringWithFormat:@"%s@%s", user->nickname, user->hostname]];
			[thisChat displayMessage:[NSString stringWithFormat:@"nickname: %s", user->nickname]];
			[thisChat displayMessage:[NSString stringWithFormat:@"realname: %s", user->realname]];
			SilcHashTableList htl2;
			silc_hash_table_list(user->channels, &htl2);
			SilcChannelEntry context2;
			NSString* channellist = [NSString string];
			while (silc_hash_table_get(&htl2, &context2, nil))
			{
				if (context2 && context2->channel_name)
					channellist = [channellist stringByAppendingString:[NSString stringWithFormat:@" %s", context2->channel_name]];
			}
			silc_hash_table_list_reset(&htl);
			[thisChat displayMessage:[NSString stringWithFormat:@"channels:%@", channellist]];
			break;
	}
	va_end(va);
}


/* Called to indicate that connection was either successfully established
   or connecting failed.  This is also the first time application receives
   the SilcClientConnection objecet which it should save somewhere.
   If the `success' is FALSE the application must always call the function
   silc_client_close_connection. */

static void
silc_connected(SilcClient client, SilcClientConnection conn,
	       SilcClientConnectionStatus status)
{
	//MyBot mybot = client->application;
	SilcBuffer idp;

	ChatController* thisChat = [[ClientManager instance] getController:client];
	if (status == SILC_CLIENT_CONN_ERROR) {
		[thisChat displayMessage:@"Could not connect to server"];
		silc_client_close_connection(client, conn);
		return;
	}

	[thisChat displayMessage:@"Connected to server."];

	/* Save the connection context */
	[thisChat setConnection:conn];

	/* Now that we are connected, join to thington channel with JOIN command. */
	//silc_client_command_call(client, conn, "JOIN thington");
}


/* Called to indicate that connection was disconnected to the server.
   The `status' may tell the reason of the disconnection, and if the
   `message' is non-NULL it may include the disconnection message
   received from server. */

static void
silc_disconnected(SilcClient client, SilcClientConnection conn,
		  SilcStatus status, const char *message)
{
	//MyBot mybot = client->application;

	/* We got disconnected from server */
	ChatController* thisChat = [[ClientManager instance] getController:client];
	[thisChat setConnection:NULL];
	char buffer[512];
	sprintf(buffer, "MyBot: %s:%s\n", silc_get_status_message(status), message);
	[thisChat displayMessage:[NSString stringWithCString:buffer]];
}


/* Find authentication method and authentication data by hostname and
   port. The hostname may be IP address as well. When the authentication
   method has been resolved the `completion' callback with the found
   authentication method and authentication data is called. The `conn'
   may be NULL. */

static void
silc_get_auth_method(SilcClient client, SilcClientConnection conn,
		     char *hostname, SilcUInt16 port,
		     SilcGetAuthMeth completion,
		     void *context)
{
  /* MyBot assumes that there is no authentication requirement in the
     server and sends nothing as authentication.  We just reply with
     TRUE, meaning we know what is the authentication method. :). */
  completion(TRUE, SILC_AUTH_NONE, NULL, 0, context);
}


/* Verifies received public key. The `conn_type' indicates which entity
   (server, client etc.) has sent the public key. If user decides to trust
   the application may save the key as trusted public key for later
   use. The `completion' must be called after the public key has been
   verified. */

static void
silc_verify_public_key(SilcClient client, SilcClientConnection conn,
		       SilcSocketType conn_type, unsigned char *pk,
		       SilcUInt32 pk_len, SilcSKEPKType pk_type,
		       SilcVerifyPublicKey completion, void *context)
{
  /* MyBot is also very trusting, so we just accept the public key
     we get here.  Of course, we would have to verify the authenticity
     of the public key but our bot is too simple for that.  We just
     reply with TRUE, meaning "yeah, we trust it". :) */
  completion(TRUE, context);
}


/* Ask (interact, that is) a passphrase from user. The passphrase is
   returned to the library by calling the `completion' callback with
   the `context'. The returned passphrase SHOULD be in UTF-8 encoded,
   if not then the library will attempt to encode. */

static void
silc_ask_passphrase(SilcClient client, SilcClientConnection conn,
		    SilcAskPassphrase completion, void *context)
{
  /* MyBot does not support asking passphrases from users since there
     is no user in our little client.  We just reply with nothing. */
  completion(NULL, 0, context);
}


/* Notifies application that failure packet was received.  This is called
   if there is some protocol active in the client.  The `protocol' is the
   protocol context.  The `failure' is opaque pointer to the failure
   indication.  Note, that the `failure' is protocol dependant and
   application must explicitly cast it to correct type.  Usually `failure'
   is 32 bit failure type (see protocol specs for all protocol failure
   types). */

static void
silc_failure(SilcClient client, SilcClientConnection conn,
	     SilcProtocol protocol, void *failure)
{
  /* Well, something bad must have happened during connecting to the
     server since we got here.  Let's just print that something failed.
     The "failure" would include more information but let's not bother
     with that now. */
  fprintf(stderr, "MyBot: Connecting failed (protocol failure)\n");
}


/* Asks whether the user would like to perform the key agreement protocol.
   This is called after we have received an key agreement packet or an
   reply to our key agreement packet. This returns TRUE if the user wants
   the library to perform the key agreement protocol and FALSE if it is not
   desired (application may start it later by calling the function
   silc_client_perform_key_agreement). If TRUE is returned also the
   `completion' and `context' arguments must be set by the application. */

static bool
silc_key_agreement(SilcClient client, SilcClientConnection conn,
		   SilcClientEntry client_entry, const char *hostname,
		   SilcUInt16 port, SilcKeyAgreementCallback *completion,
		   void **context)
{
  /* MyBot does not support incoming key agreement protocols, it's too
     simple for that. */
  return FALSE;
}


/* Notifies application that file transfer protocol session is being
   requested by the remote client indicated by the `client_entry' from
   the `hostname' and `port'. The `session_id' is the file transfer
   session and it can be used to either accept or reject the file
   transfer request, by calling the silc_client_file_receive or
   silc_client_file_close, respectively. */

static void
silc_ftp(SilcClient client, SilcClientConnection conn,
	 SilcClientEntry client_entry, SilcUInt32 session_id,
	 const char *hostname, SilcUInt16 port)
{
  /* MyBot does not support file transfer, it's too simple for that too. */
}


/* Delivers SILC session detachment data indicated by `detach_data' to the
   application.  If application has issued SILC_COMMAND_DETACH command
   the client session in the SILC network is not quit.  The client remains
   in the network but is detached.  The detachment data may be used later
   to resume the session in the SILC Network.  The appliation is
   responsible of saving the `detach_data', to for example in a file.

   The detachment data can be given as argument to the functions
   silc_client_connect_to_server, or silc_client_add_connection when
   creating connection to remote server, inside SilcClientConnectionParams
   structure.  If it is provided the client library will attempt to resume
   the session in the network.  After the connection is created
   successfully, the application is responsible of setting the user
   interface for user into the same state it was before detaching (showing
   same channels, channel modes, etc).  It can do this by fetching the
   information (like joined channels) from the client library. */

static void
silc_detach(SilcClient client, SilcClientConnection conn,
	    const unsigned char *detach_data, SilcUInt32 detach_data_len)
{
  /* Oh, and MyBot does not support session detaching either. */
}

void silc_parse_nickname(const char* nickname, char** ret_nickname)
{
	NSLog(@"Trying to parse nickname %s.", nickname);
	*ret_nickname = strdup(nickname);
}

/* Our client operations for the MyBot.  This structure is filled with
   functions and given as argument to the silc_client_alloc function.
   Even though our little bot does not need all these functions we must
   provide them since the SILC Client Library wants them all. */
/* This structure and all the functions were taken from the
   lib/silcclient/client_ops_example.c. */
SilcClientOperations opers = {
  silc_say,
  silc_channel_message,
  silc_private_message,
  silc_notify,
  silc_command,
  silc_command_reply,
  silc_connected,
  silc_disconnected,
  silc_get_auth_method,
  silc_verify_public_key,
  silc_ask_passphrase,
  silc_failure,
  silc_key_agreement,
  silc_ftp,
  silc_detach
};