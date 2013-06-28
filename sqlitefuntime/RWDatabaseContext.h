//
//  RWDatabaseContext.h
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/20/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

@interface RWDatabaseContext : NSObject

@property sqlite3 *dbContext;

+ (RWDatabaseContext *) singleton;
@end
