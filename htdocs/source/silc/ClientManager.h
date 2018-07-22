//
//  ClientManager.h
//  SILC Aqua
//
//  Created by Matthew Hielscher on 3/22/05.
//  Copyright 2005 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>

// ClientManager is a singleton. See below for the static global instance variable.
@interface ClientManager : NSObject {
	NSMutableArray* clientList;
}

+ (ClientManager*)instance;
- (id)init;
- (ChatController*)getController:(SilcClient)client;
- (void)add:(ChatController*)controller;
- (ChatController*)remove:(ChatController*)controller;
- (ChatController*)removeByClient:(SilcClient)client;

@end

//The singleton variable
static ClientManager* theManager = nil;

static void silc_say(SilcClient client, SilcClientConnection conn, SilcClientMessageType type, char *msg, ...);

static void
silc_channel_message(SilcClient client, SilcClientConnection conn,
		     SilcClientEntry sender, SilcChannelEntry channel,
		     SilcMessagePayload payload, SilcChannelPrivateKey key,
		     SilcMessageFlags flags, const unsigned char *message,
		     SilcUInt32 message_len);

static void
silc_private_message(SilcClient client, SilcClientConnection conn,
		     SilcClientEntry sender, SilcMessagePayload payload,
		     SilcMessageFlags flags,
		     const unsigned char *message,
		     SilcUInt32 message_len);

static void
silc_notify(SilcClient client, SilcClientConnection conn,
	    SilcNotifyType type, ...);

static void
silc_command(SilcClient client, SilcClientConnection conn,
	     SilcClientCommandContext cmd_context, bool success,
	     SilcCommand command, SilcStatus status);

static void
silc_command_reply(SilcClient client, SilcClientConnection conn,
		   SilcCommandPayload cmd_payload, bool success,
		   SilcCommand command, SilcStatus status, ...);

static void
silc_connected(SilcClient client, SilcClientConnection conn,
	       SilcClientConnectionStatus status);

static void
silc_disconnected(SilcClient client, SilcClientConnection conn,
		  SilcStatus status, const char *message);

static void
silc_get_auth_method(SilcClient client, SilcClientConnection conn,
		     char *hostname, SilcUInt16 port,
		     SilcGetAuthMeth completion,
		     void *context);

static void
silc_verify_public_key(SilcClient client, SilcClientConnection conn,
		       SilcSocketType conn_type, unsigned char *pk,
		       SilcUInt32 pk_len, SilcSKEPKType pk_type,
		       SilcVerifyPublicKey completion, void *context);

static void
silc_ask_passphrase(SilcClient client, SilcClientConnection conn,
		    SilcAskPassphrase completion, void *context);

static void
silc_failure(SilcClient client, SilcClientConnection conn,
	     SilcProtocol protocol, void *failure);

static bool
silc_key_agreement(SilcClient client, SilcClientConnection conn,
		   SilcClientEntry client_entry, const char *hostname,
		   SilcUInt16 port, SilcKeyAgreementCallback *completion,
		   void **context);

static void
silc_ftp(SilcClient client, SilcClientConnection conn,
	 SilcClientEntry client_entry, SilcUInt32 session_id,
	 const char *hostname, SilcUInt16 port);

static void
silc_detach(SilcClient client, SilcClientConnection conn,
	    const unsigned char *detach_data, SilcUInt32 detach_data_len);
		
void silc_parse_nickname(const char* nickname, char** ret_nickname);
		
extern SilcClientOperations opers;