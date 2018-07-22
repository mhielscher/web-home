//
//  UserList.m
//  SILC Aqua
//
//  Created by Matthew Hielscher on 4/9/05.
//  Copyright 2005 __MyCompanyName__. All rights reserved.
//

#import "includes/silcincludes.h"
#import "silcclient.h"
#import "UserList.h"


@implementation UserList

- (id)init
{
	if (![super init])
		return nil;
	users = [[NSMutableArray alloc] initWithCapacity:1];
	return self;
}

- (NSString*)getUser:(int)index
{
	return [users objectAtIndex:index];
}

- (void)add:(NSString*)user
{
	[users addObject:user];
}

- (NSString*)remove:(NSString*)nick
{
	[users removeObject:nick];
	return nick;
}

- (void)clear
{
	[users removeAllObjects];
}

- (int)numberOfRowsInTableView:(NSTableView*)aTableView
{
	return [users count];
}

- (id)tableView:(NSTableView*)aTableView objectValueForTableColumn:(NSTableColumn*)aTableColumn row:(int)rowIndex
{
	return [self getUser:rowIndex];
}


@end
