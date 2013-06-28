//
//  RWPageViewController.h
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/19/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RWPageModel.h"

@interface RWPageViewController : UIViewController
@property (nonatomic) CGRect originalTextViewFrame;
@property (nonatomic) UITextView *textView;
@property (atomic) RWPageModel *page;

@property (nonatomic, retain) UIBarButtonItem *save;

@property (nonatomic) IBOutlet UITextField *titleField;
@property (nonatomic) IBOutlet UITextField *categoryField;
@property (nonatomic) IBOutlet UITextView *bodyField;

- (IBAction)savePage:(id)sender;
@end
