//
//  UserList.h
//  SILC Aqua
//
//  Created by Matthew Hielscher on 4/9/05.
//  Copyright 2005 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>


@interface UserList : NSObject {
	NSMutableArray* users;
}

- (id)init;
- (NSString*)getUser:(int)index;
- (void)add:(NSString*)user;
- (NSString*)remove:(NSString*)nick;
- (void)clear;

@end
