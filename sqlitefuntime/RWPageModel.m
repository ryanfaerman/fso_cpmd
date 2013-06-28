//
//  RWPageModel.m
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/20/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import "RWPageModel.h"
#import <sqlite3.h>
#import "RWDatabaseContext.h"

@implementation RWPageModel

@synthesize page_id,title, body, category, dirty, created_at, updated_at, new_object, remote_id;


- (RWPageModel*) initWithDictionary:(NSDictionary *)values
{
  if (self = [super init]) {
    
    [RWPageModel transaction:^(sqlite3 *dbContext) {
      const char *createTableStatement = "create table if not exists pages (id integer primary key autoincrement, remote_id varchar(255), title varchar(255), body text, dirty boolean default true, updated_at integer, category varchar(255))";
      char *error;
      sqlite3_exec(dbContext, createTableStatement, NULL, NULL, &error);
    
    }];
    
    dirty = YES;
    new_object = YES;
    
    page_id = [values objectForKey:@"page_id"];
    remote_id = [values objectForKey:@"remote_id"];
    title = [values objectForKey:@"title"];
    body = [values objectForKey:@"body"];
    category = [values objectForKey:@"category"];
    title = [values objectForKey:@"title"];
    dirty = [[values objectForKey:@"dirty"] boolValue];
    category = [values objectForKey:@"category"];
    updated_at = [values objectForKey:@"updated_at"];
//    new_object = page_id == nil;
  }
  
  return self;
}

+ (NSArray*) findWhere:(NSDictionary *)conditions orderBy:(NSString *)orders
{
  NSMutableString *query = [[NSMutableString alloc] initWithString:@"SELECT id,remote_id,title,body,dirty,updated_at,category FROM pages"];
  
  if(conditions != nil) {
    [query appendString:@" where "];
    
    NSEnumerator *enumerator = [conditions keyEnumerator];
    id key;
    
    int k = 0;
    while ((key = [enumerator nextObject])) {
      NSLog(@"KEY: %@", key);
      if(k > 0) {
        [query appendString:@" and "];
      }
      
      [query appendFormat:@"%@='%@'",key, [conditions objectForKey:key]];
      k++;
    }

  }
  
  if(orders != nil) {
    [query appendFormat:@" order by %@", orders];
  }
  
  NSLog(@"%@", query);
  
  NSMutableArray *output = [[NSMutableArray alloc] init];
  
  [RWPageModel transaction:^(sqlite3 *dbContext) {
      sqlite3_stmt *compiledQuery;
      sqlite3_prepare_v2(dbContext, [query UTF8String], -1, &compiledQuery, NULL);
      
      RWPageModel *page;
      
      while(sqlite3_step(compiledQuery) == SQLITE_ROW) {
        page = [[RWPageModel alloc] init];
        page.page_id = [[NSNumber alloc] initWithUnsignedInt:sqlite3_column_int(compiledQuery, 1)];
        page.remote_id = [[NSString alloc] initWithUTF8String:(const char*)sqlite3_column_text(compiledQuery, 2)];
        page.title = [[NSString alloc] initWithUTF8String:(const char*)sqlite3_column_text(compiledQuery, 2)];
        page.body = [[NSString alloc] initWithUTF8String:(const char*)sqlite3_column_text(compiledQuery, 3)];
        page.dirty = [[[NSNumber alloc] initWithUnsignedInt:sqlite3_column_int(compiledQuery, 5)] boolValue];
        page.updated_at = [[NSDate alloc] initWithTimeIntervalSince1970:sqlite3_column_int(compiledQuery, 6)];
        page.category = [[NSString alloc] initWithUTF8String:(const char*)sqlite3_column_text(compiledQuery, 6)];
        page.new_object = NO;
        
        [output addObject:page];
        
      }
      

  }];
  
  return [NSArray arrayWithArray: output];
}

+ (NSArray*) find:(NSDictionary*)conditions
{
  return [RWPageModel findWhere:conditions orderBy:nil];
}

+ (NSArray*) findAll
{
  return [RWPageModel findWhere:nil orderBy:nil];
}

- (NSNumber*) saveWithoutPush
{
  NSNumber *returned_id;
  
  NSArray *dirpaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
  if(dirpaths != nil) {
    NSString *documentsDirectory = [dirpaths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"db.sqlite"];
    
    sqlite3 *dbContext;
    
    if(sqlite3_open([dbPath UTF8String], &dbContext) == SQLITE_OK) {
      
    const char *insert_sql = "insert into pages (remote_id, title, body, dirty, updated_at, category, id) values(?,?,?,?,?,?,?)";
    const char *update_sql = "update pages set remote_id=?, title=?, body=?, dirty=?, updated_at=?, category=? where id=?";
    sqlite3_stmt *compiledStatement;
    NSLog(@"new object? %c", new_object);
    if (new_object == nil || new_object == YES) {
      NSLog(@"NEW OBJECT USING INSERT");
      sqlite3_prepare_v2(dbContext, insert_sql, -1, &compiledStatement, NULL);
      
    } else {
      NSLog(@"EXISTING OBJECT USING UPDATE");
      sqlite3_prepare_v2(dbContext, update_sql, -1, &compiledStatement, NULL);
    }
    NSLog(@"remote_id: %@", remote_id);
    sqlite3_bind_text(compiledStatement, 1, [remote_id UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(compiledStatement, 2, [title UTF8String], -1, SQLITE_TRANSIENT);
    
    sqlite3_bind_text(compiledStatement, 3, [body UTF8String], -1, SQLITE_TRANSIENT);
    
    int dirty_int = dirty ? 1 : 0;
    sqlite3_bind_int(compiledStatement, 4, dirty_int);
    sqlite3_bind_int(compiledStatement, 5, [[[NSDate alloc] init] timeIntervalSince1970]);
    sqlite3_bind_text(compiledStatement, 6, [category UTF8String], -1, SQLITE_TRANSIENT);
    
    int page_id_int;
    if (page_id != nil) {
      page_id_int = [page_id integerValue];
    }
    sqlite3_bind_int(compiledStatement, 7, page_id_int);
    
    
    
    sqlite3_step(compiledStatement);
    sqlite3_finalize(compiledStatement);
    if (new_object == nil || new_object == YES) {
      new_object = NO;
      page_id = [[NSNumber alloc] initWithLongLong: sqlite3_last_insert_rowid(dbContext)];
    }

  }
  }


  NSLog(@"Done: %@", page_id);
  
  return returned_id;
}

- (BOOL) save
{
  [self saveWithoutPush];
  [self push];
  return YES;
}

- (BOOL) pull
{
  return YES;
}
- (BOOL) push
{
  NSLog(@"Pushing to Parse");
  NSDictionary *values = [[NSDictionary alloc]
                          initWithObjects: [NSArray arrayWithObjects: title, body, category, nil]
                          forKeys: [NSArray arrayWithObjects:@"title", @"body", @"category", nil]];
  
  if (new_object == nil || new_object == YES || remote_id == nil || [remote_id isEqualToString:@""]) {
    NSLog(@"saving new object");
  } else {
    NSLog(@"saving existing object (update)");
    [values setValue:remote_id forKey:@"objectId"];
  }
  
  PFObject *testObject = [PFObject objectWithClassName:@"Page"];
  [testObject setValuesForKeysWithDictionary:values];
  [testObject save];
  NSLog(@"succeeded with push, remote id: %@, %@", [testObject objectId], page_id);
  remote_id = [testObject objectId];
  dirty = NO;
  new_object = NO;
  [self saveWithoutPush];
  
//  test
//   ^(BOOL succeeded, NSError *error) {
//    if(succeeded) {
//      NSLog(@"succeeded with push, existing remote id: %@, %@", remote_id, page_id);
//      NSLog(@"succeeded with push, remote id: %@", [testObject objectId]);
//      remote_id = [testObject objectId];
//      new_object = NO;
//      dirty = NO;
//      [self saveWithoutPush];
//    }
//    
//  }];
  return YES;
}
- (BOOL) reload
{
  return YES;
}
- (BOOL) destroy
{
  
  NSLog(@"Destroying...");
  [RWPageModel transaction:^(sqlite3 *dbContext) {
    const char *delete_sql = "delete from pages where id=?";
    sqlite3_stmt *compiledStatement;
    sqlite3_prepare_v2(dbContext, delete_sql, -1, &compiledStatement, NULL);
    sqlite3_bind_int(compiledStatement, 1, [page_id integerValue]);
    
    sqlite3_step(compiledStatement);
    sqlite3_finalize(compiledStatement);
  }];
  return YES;
}

- (NSNumber*) transaction:(void (^)(sqlite3 *))block
{
  NSNumber *output;
  
  NSArray *dirpaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
  if(dirpaths != nil) {
    NSString *documentsDirectory = [dirpaths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"db.sqlite"];
    
    sqlite3 *dbContext;
    
    if(sqlite3_open([dbPath UTF8String], &dbContext) == SQLITE_OK) {
      if(block) block(dbContext);
      
      output = [[NSNumber alloc] initWithLongLong: sqlite3_last_insert_rowid(dbContext)];
      sqlite3_close(dbContext);
    }
  }
  return output;
}

+ (NSNumber*) transaction:(void (^)(sqlite3 *))block
{
  NSNumber *output;
  
  NSArray *dirpaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
  if(dirpaths != nil) {
    NSString *documentsDirectory = [dirpaths objectAtIndex:0];
    NSString *dbPath = [documentsDirectory stringByAppendingPathComponent:@"db.sqlite"];
    
    sqlite3 *dbContext;
    
    if(sqlite3_open([dbPath UTF8String], &dbContext) == SQLITE_OK) {
      if(block) block(dbContext);
      
      output = [[NSNumber alloc] initWithLongLong: sqlite3_last_insert_rowid(dbContext)];
      sqlite3_close(dbContext);
    }
  }
  return output;
}

+ (void) forceReload
{
  [RWPageModel transaction:^(sqlite3 *dbContext) {
    const char *delete_sql = "delete from pages";
    sqlite3_stmt *compiledStatement;
    sqlite3_prepare_v2(dbContext, delete_sql, -1, &compiledStatement, NULL);
    sqlite3_step(compiledStatement);
    sqlite3_finalize(compiledStatement);
  }];
  
  PFQuery *query = [PFQuery queryWithClassName:@"Page"];
  
  NSDictionary *values;
  RWPageModel *page;
  for (PFObject *object in [query findObjects]) {
    values = [[NSDictionary alloc]
              initWithObjects:[NSArray arrayWithObjects:object.objectId,[object objectForKey:@"title"],[object objectForKey:@"body"],[object objectForKey:@"category"],object.updatedAt, nil]
              forKeys:[NSArray arrayWithObjects:@"remote_id",@"title",@"body",@"category",@"updated_at", nil]];
    page = [[RWPageModel alloc] initWithDictionary:values];
    [page save];
//    , , , , 
//    , ,,, [object objectForKey:@"updated_at"]
  }
}

@end
