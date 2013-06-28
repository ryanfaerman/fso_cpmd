//
//  RWPageModel.h
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/20/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Parse/Parse.h>
#import <sqlite3.h>

@interface RWPageModel : NSObject


@property (atomic) NSNumber *page_id;
@property (atomic) NSString *remote_id;
@property (atomic) NSString *title;
@property (atomic) NSString *body;
@property (atomic) NSString *category;
@property (atomic) BOOL globally_dirty;
@property (atomic) BOOL dirty;
@property (atomic) BOOL new_object;
@property (atomic) NSDate *created_at;
@property (atomic) NSDate *updated_at;

- (RWPageModel*) initWithDictionary:(NSDictionary*)values;
- (BOOL) save;
- (NSNumber*) saveWithoutPush;
- (BOOL) pull;
- (BOOL) push;
- (BOOL) reload;
- (BOOL) destroy;

- (NSNumber*) transaction:(void (^)(sqlite3 *))block;
+ (NSNumber*) transaction:(void (^)(sqlite3 *))block;

+ (NSArray*) findAll;
+ (NSArray*) find:(NSDictionary*)conditions;
+ (NSArray*) findWhere:(NSDictionary*)conditions orderBy:(NSString*) orders;

+ (void) forceReload;
@end
