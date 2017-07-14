
#import "CFTextView.h"
#import "UIImage+GIF.h"
#import "CFTextAttachment.h"

@implementation CFTextView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame])
    {
        
    }
    return self;
}

- (void)setAttributedText:(NSAttributedString *)attributedText
{
    [super setAttributedText:attributedText];
    for (UIView* subView in self.subviews) {
        if ([subView isKindOfClass:[UIImageView class]]) {
            [subView removeFromSuperview];
        }
    }
    
    [self.attributedText enumerateAttribute:NSAttachmentAttributeName inRange:NSMakeRange(0, self.attributedText.length) options:NSAttributedStringEnumerationReverse usingBlock:^(CFTextAttachment* value, NSRange range, BOOL * _Nonnull stop) {
        if (value && CGRectEqualToRect(value.bounds, gifRect)) {
            self.selectedRange = range;

            UITextPosition *beginning = self.beginningOfDocument;
            UITextPosition *start = [self positionFromPosition:beginning offset:range.location];
            CGRect rect = [self caretRectForPosition:start];
            
            rect.origin.y += (rect.size.height - gifRect.size.height);
            rect.size = gifRect.size;
            
            UIImageView* imageView = [[UIImageView alloc] init];
            [self addSubview:imageView];
            imageView.frame = rect;
            imageView.image = [UIImage sd_animatedGIFNamed:value.gifName];
            //            NSLog(@"\n rect = %@ \ncount = %@", NSStringFromCGRect(rect),  [self selectionRectsForRange:self.selectedTextRange]);
            
            //            imageView.backgroundColor = [UIColor greenColor];
        }
    }];
    self.selectedRange = NSMakeRange(0, 0);
}
@end
