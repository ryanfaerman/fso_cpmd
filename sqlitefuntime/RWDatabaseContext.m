//
//  RWDatabaseContext.m
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/20/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import "RWDatabaseContext.h"
#import <sqlite3.h>

@implementation RWDatabaseContext

@synthesize dbContext;

- (id)init
{
  if ((self = [super init])) {
    NSArray *dirpaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    if(dirpaths != nil) {
      NSString *documentsDirectory = [dirpaths objectAtIndex:0];
      NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"db.sqlite"];
      
      NSLog(@"dbpath: %@", dbPath);
      
      if(sqlite3_open([dbPath UTF8String], &dbContext) == SQLITE_OK) {
        NSLog(@"database good to go boss");
      }
    }
  }
  return self;
}

+ (RWDatabaseContext *) singleton
{
  static RWDatabaseContext *instance;
  
  @synchronized(self) {
    if (!instance) {
      instance = [[RWDatabaseContext alloc] init];
    }
  }
  
  return instance;
}

@end
