//
//  RWViewController.m
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/19/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import "RWViewController.h"
#import <sqlite3.h>
#import "RWPageModel.h"
#import "RWDatabaseContext.h"

@interface RWViewController ()

@end

@implementation RWViewController

@synthesize pageList, mode, pageView, pages, shortAlphaButton, shortDateButton;


#pragma mark - View Lifecycle
- (void)viewDidLoad
{
  
  [super viewDidLoad];
  
  pageView = [[RWPageViewController alloc] initWithNibName:@"RWPageViewController"
                                                    bundle:nil];
	
  mode = [[UIBarButtonItem alloc] initWithTitle:@"Edit"
                                          style:UIBarButtonItemStylePlain
                                         target:self
                                         action:@selector(toggleEditMode:)];
  
  UIBarButtonItem *addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCompose
                                                                             target:self
                                                                             action:@selector(newPageHandler:)];
  
  
  NSDictionary *values = [[NSDictionary alloc] initWithObjects:[NSArray arrayWithObjects:@"stan", @"my body", @"movies", nil] forKeys:[NSArray arrayWithObjects:@"title", @"body", @"category", nil]];
  [RWPageModel forceReload];
  RWPageModel *page = [[RWPageModel alloc] initWithDictionary:values];
  NSLog(@"Page Title: %@", page.title);
  
  //  [testObject setObject:@"bar" forKey:@"foo"];
  //  [testObject save];
//  [page save];
  

  
  self.navigationItem.rightBarButtonItem = mode;
  self.navigationItem.leftBarButtonItem = addButton;
  self.title = @"Page List";
  
}

-(void)viewWillAppear:(BOOL)animated
{
  [super viewWillAppear:animated];
  pages = [[NSMutableArray alloc] initWithArray:[RWPageModel find: nil]];
  [pageList reloadData];
}

#pragma mark - Button Handlers
-(IBAction)toggleEditMode:(id)sender
{
  inEditMode = !inEditMode;
  if (inEditMode) {
    [mode setTitle:@"Done"];
  } else {
    [mode setTitle:@"Edit"];
  }
  [pageList setEditing:inEditMode animated:YES];
}

-(IBAction)newPageHandler:(id)sender
{
  pageView.page = [[RWPageModel alloc] init];
  [self.navigationController pushViewController:pageView animated:YES];
}

- (IBAction)changeSort:(id)sender
{
  UIButton *daBut = sender;
  switch (daBut.tag) {
    case 1:
      // by date
      pages = [[NSMutableArray alloc] initWithArray:[RWPageModel findWhere:nil orderBy: @"updated_at DESC"]];
      
      break;
      
    default:
      // by alpha
      pages = [[NSMutableArray alloc] initWithArray:[RWPageModel findWhere:nil orderBy: @"title asc"]];
      break;
  }
  
  [pageList reloadData];

}


#pragma mark - Housekeeping

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
  return [pages count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
  static NSString *CellIdentifier = @"Cell";
  
  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
  if (cell == nil) {
    cell = [[UITableViewCell alloc]
            initWithStyle:UITableViewCellStyleSubtitle
            reuseIdentifier:CellIdentifier];
  }
  
  RWPageModel *page = [pages objectAtIndex:indexPath.row];
  
  cell.textLabel.text = page.title;
  cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
  cell.detailTextLabel.text = page.category;
  
  return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
  // Unhighlight the row
  [tableView deselectRowAtIndexPath:indexPath animated:YES];
  pageView.page = [pages objectAtIndex:indexPath.row];
  [self.navigationController pushViewController:pageView animated:YES];
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
  return UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
  if (editingStyle == UITableViewCellEditingStyleDelete) {
    RWPageModel *page = [pages objectAtIndex:indexPath.row];
    [page destroy];
    [pages removeObjectAtIndex:indexPath.row];
    [pageList deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath]
                     withRowAnimation:UITableViewRowAnimationFade];
    
  }
}



@end
