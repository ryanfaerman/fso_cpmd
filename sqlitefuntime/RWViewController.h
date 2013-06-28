//
//  RWViewController.h
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/19/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Parse/Parse.h>
#import <sqlite3.h>

#import "RWPageViewController.h"

@interface RWViewController : UIViewController <UITableViewDelegate>
{
  BOOL inEditMode;
  sqlite3 *dbContext;
}

@property (nonatomic, retain) IBOutlet UITableView *pageList;
@property (nonatomic, retain) UIBarButtonItem *mode;
@property (nonatomic, retain) NSMutableArray *pages;
@property (nonatomic, retain) RWPageViewController *pageView;
@property (nonatomic, retain) IBOutlet UIButton *shortAlphaButton;
@property (nonatomic, retain) IBOutlet UIButton *shortDateButton;


- (IBAction)toggleEditMode:(id)sender;
- (IBAction)newPageHandler:(id)sender;
- (IBAction)changeSort:(id)sender;

@end
