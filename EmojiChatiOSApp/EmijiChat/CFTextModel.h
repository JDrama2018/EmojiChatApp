
#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>

@interface CFTextModel : NSObject

@property (nonatomic, strong) NSMutableAttributedString *attributedString;
@property (nonatomic, copy) NSString *contentString;
@property (nonatomic, copy) NSString *contentStringInChat;
@property (nonatomic, copy) NSString *contentStringSingleInChat;
@property (nonatomic, copy) NSString *contentStringInEditing;
@property (nonatomic, assign) CGFloat height;
@end
