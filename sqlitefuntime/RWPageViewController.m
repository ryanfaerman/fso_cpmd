//
//  RWPageViewController.m
//  sqlitefuntime
//
//  Created by Ryan Faerman on 6/19/13.
//  Copyright (c) 2013 Ryan Faerman. All rights reserved.
//

#import "RWPageViewController.h"

@interface RWPageViewController ()

@end

@implementation RWPageViewController
@synthesize originalTextViewFrame, textView, titleField, categoryField, bodyField, page,save;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
      save = [[UIBarButtonItem alloc] initWithTitle:@"Save"
                                              style:UIBarButtonItemStylePlain
                                             target:self
                                             action:@selector(savePage:)];
      self.navigationItem.rightBarButtonItem = save;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

-(IBAction)savePage:(id)sender {
  page.title = titleField.text;
  page.category = categoryField.text;
  page.body = bodyField.text;
  
  [page save];
}

- (void)viewWillAppear:(BOOL)animated
{
  // Register notifications for when the keyboard appears
  
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
  [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
  titleField.text = page.title;
  categoryField.text = page.category;
  bodyField.text = page.body;
}

- (void)viewWillDisappear:(BOOL)animated {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)keyboardWillShow:(NSNotification*)notification {
  [self moveTextViewForKeyboard:notification up:YES];
}

- (void)keyboardWillHide:(NSNotification*)notification {
  [self moveTextViewForKeyboard:notification up:NO];
}

- (void)moveTextViewForKeyboard:(NSNotification*)notification up:(BOOL)up {
  NSDictionary *userInfo = [notification userInfo];
  NSTimeInterval animationDuration;
  UIViewAnimationCurve animationCurve;
  CGRect keyboardRect;
  
  [[userInfo objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
  animationDuration = [[userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
  keyboardRect = [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
  keyboardRect = [self.view convertRect:keyboardRect fromView:nil];
  
  [UIView beginAnimations:@"ResizeForKeyboard" context:nil];
  [UIView setAnimationDuration:animationDuration];
  [UIView setAnimationCurve:animationCurve];
  
  if (up == YES) {
    CGFloat keyboardTop = keyboardRect.origin.y;
    CGRect newTextViewFrame = textView.frame;
    originalTextViewFrame = self.textView.frame;
    newTextViewFrame.size.height = keyboardTop - textView.frame.origin.y - 10;
    
    textView.frame = newTextViewFrame;
  } else {
    // Keyboard is going away (down) - restore original frame
    textView.frame = originalTextViewFrame;
  }
  
  [UIView commitAnimations];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
