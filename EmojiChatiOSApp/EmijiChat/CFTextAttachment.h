
#import <UIKit/UIKit.h>

#define gifRect CGRectMake(0, 0, 25, 25)
#define gifRectInChat CGRectMake(0, 0, 30, 30)
#define gifRectSingleInChat CGRectMake(0, 0, 150, 150)

@interface CFTextAttachment : NSTextAttachment
@property (nonatomic, copy) NSString *gifName;
@end
