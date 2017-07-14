
#import "CFTextModel.h"
#import <UIKit/UIKit.h>
#import "CFTextAttachment.h"

@implementation CFTextModel

- (void)setContentString:(NSString *)contentString
{
    _contentString = contentString;
    NSString* pattern = @"(\\(emoji_\\d\\d?_\\d\\d?\\_\\d\\d?\\))";
    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
    NSMutableDictionary* gifEomtionDict = [[NSMutableDictionary alloc] init];
    [regx enumerateMatchesInString:contentString options:NSMatchingReportProgress range:NSMakeRange(0, contentString.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
        
        NSString* resultString = [contentString substringWithRange:result.range];
        NSString* gifName = emotionDic[resultString];
        
        for (int i = 0; resultString.length > 2 && !gifName; i++)
        {
            resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
            gifName = emotionDic[resultString];
        }
        
        if (gifName)
        {
            gifEomtionDict[NSStringFromRange(NSMakeRange(result.range.location, resultString.length))] = gifName;
        }
    }];
    
    NSMutableAttributedString* attributedString = [[NSMutableAttributedString alloc] initWithString:contentString];
    NSMutableArray* ranges = [gifEomtionDict.allKeys mutableCopy];
    [ranges sortUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2)
    {
        NSRange range1 = NSRangeFromString(obj1);
        NSRange range2 = NSRangeFromString(obj2);
        
        if (range1.location < range2.location)
        {
            return NSOrderedDescending;
        }
        
        return NSOrderedAscending;
    }];
    
    for (NSString* rangeString in ranges)
    {
        CFTextAttachment* attachment    = [[CFTextAttachment alloc] init];
        attachment.bounds               = gifRect;
        attachment.gifName              = gifEomtionDict[rangeString];
        NSAttributedString* attachmentString = [NSAttributedString attributedStringWithAttachment:attachment];
        [attributedString replaceCharactersInRange:NSRangeFromString(rangeString) withAttributedString:attachmentString];
    }
    
    [attributedString addAttribute:NSForegroundColorAttributeName value:[UIColor colorWithRed:126.0f/255.0f green:138.0f/255.0f blue:153.0f/255.0f alpha:1] range:NSMakeRange(0, attributedString.length)];
    [attributedString addAttribute:NSFontAttributeName value:[UIFont fontWithName:@"Avenir" size:14] range:NSMakeRange(0, attributedString.length)];
    
    NSMutableParagraphStyle* paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    [paragraphStyle setParagraphStyle:[NSParagraphStyle defaultParagraphStyle]];
    paragraphStyle.lineBreakMode = NSLineBreakByCharWrapping;

    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, attributedString.length)];
    
    self.attributedString = attributedString;
}

- (void)setContentStringInChat:(NSString *)contentString
{
    _contentString = contentString;

    NSString* pattern = @"(\\(emoji_\\d\\d?_\\d\\d?\\_\\d\\d?\\))";
    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
    NSMutableDictionary* gifEomtionDict = [[NSMutableDictionary alloc] init];
    [regx enumerateMatchesInString:contentString options:NSMatchingReportProgress range:NSMakeRange(0, contentString.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
        
        NSString* resultString = [contentString substringWithRange:result.range];
        NSString* gifName = emotionDic[resultString];
        
        for (int i = 0; resultString.length > 2 && !gifName; i++)
        {
            resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
            gifName = emotionDic[resultString];
        }
        
        if (gifName)
        {
            gifEomtionDict[NSStringFromRange(NSMakeRange(result.range.location, resultString.length))] = gifName;
        }
    }];
    
    NSMutableAttributedString* attributedString = [[NSMutableAttributedString alloc] initWithString:contentString];
    NSMutableArray* ranges = [gifEomtionDict.allKeys mutableCopy];
    [ranges sortUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2)
     {
         NSRange range1 = NSRangeFromString(obj1);
         NSRange range2 = NSRangeFromString(obj2);
         
         if (range1.location < range2.location)
         {
             return NSOrderedDescending;
         }
         
         return NSOrderedAscending;
     }];
    
    for (NSString* rangeString in ranges)
    {
        CFTextAttachment* attachment    = [[CFTextAttachment alloc] init];
        attachment.bounds               = gifRectInChat;
        attachment.gifName              = gifEomtionDict[rangeString];
        NSAttributedString* attachmentString = [NSAttributedString attributedStringWithAttachment:attachment];
        [attributedString replaceCharactersInRange:NSRangeFromString(rangeString) withAttributedString:attachmentString];
    }
    
    [attributedString addAttribute:NSFontAttributeName value:[UIFont fontWithName:@"Avenir" size:14] range:NSMakeRange(0, attributedString.length)];
    
    NSMutableParagraphStyle* paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    [paragraphStyle setParagraphStyle:[NSParagraphStyle defaultParagraphStyle]];
    paragraphStyle.lineBreakMode = NSLineBreakByCharWrapping;
    
    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, attributedString.length)];
    
    self.attributedString = attributedString;
}

- (void)setContentStringSingleInChat:(NSString *)contentString
{
    _contentString = contentString;
    
    NSString* pattern = @"(\\(emoji_\\d\\d?_\\d\\d?\\_\\d\\d?\\))";
    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
    NSMutableDictionary* gifEomtionDict = [[NSMutableDictionary alloc] init];
    [regx enumerateMatchesInString:contentString options:NSMatchingReportProgress range:NSMakeRange(0, contentString.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
        
        NSString* resultString = [contentString substringWithRange:result.range];
        NSString* gifName = emotionDic[resultString];
        
        for (int i = 0; resultString.length > 2 && !gifName; i++)
        {
            resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
            gifName = emotionDic[resultString];
        }
        
        if (gifName)
        {
            gifEomtionDict[NSStringFromRange(NSMakeRange(result.range.location, resultString.length))] = gifName;
        }
    }];
    
    NSMutableAttributedString* attributedString = [[NSMutableAttributedString alloc] initWithString:contentString];
    NSMutableArray* ranges = [gifEomtionDict.allKeys mutableCopy];
    [ranges sortUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2)
     {
         NSRange range1 = NSRangeFromString(obj1);
         NSRange range2 = NSRangeFromString(obj2);
         
         if (range1.location < range2.location)
         {
             return NSOrderedDescending;
         }
         
         return NSOrderedAscending;
     }];
    
    for (NSString* rangeString in ranges)
    {
        CFTextAttachment* attachment    = [[CFTextAttachment alloc] init];
        attachment.bounds               = gifRectSingleInChat;
        attachment.gifName              = gifEomtionDict[rangeString];
        NSAttributedString* attachmentString = [NSAttributedString attributedStringWithAttachment:attachment];
        [attributedString replaceCharactersInRange:NSRangeFromString(rangeString) withAttributedString:attachmentString];
    }
    
    NSMutableParagraphStyle* paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    [paragraphStyle setParagraphStyle:[NSParagraphStyle defaultParagraphStyle]];
    paragraphStyle.lineBreakMode = NSLineBreakByCharWrapping;
    
    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, attributedString.length)];
    
    self.attributedString = attributedString;
}

- (void)setContentStringInEditing:(NSString *)contentString
{
    _contentString = contentString;
    NSString* pattern = @"(\\(emoji_\\d\\d?_\\d\\d?\\_\\d\\d?\\))";
    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
    NSMutableDictionary* gifEomtionDict = [[NSMutableDictionary alloc] init];
    [regx enumerateMatchesInString:contentString options:NSMatchingReportProgress range:NSMakeRange(0, contentString.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
        
        NSString* resultString = [contentString substringWithRange:result.range];
        NSString* gifName = emotionDic[resultString];
        
        for (int i = 0; resultString.length > 2 && !gifName; i++)
        {
            resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
            gifName = emotionDic[resultString];
        }
        
        if (gifName)
        {
            gifEomtionDict[NSStringFromRange(NSMakeRange(result.range.location, resultString.length))] = gifName;
        }
    }];
    
    NSMutableAttributedString* attributedString = [[NSMutableAttributedString alloc] initWithString:contentString];
    NSMutableArray* ranges = [gifEomtionDict.allKeys mutableCopy];
    [ranges sortUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2)
     {
         NSRange range1 = NSRangeFromString(obj1);
         NSRange range2 = NSRangeFromString(obj2);
         
         if (range1.location < range2.location)
         {
             return NSOrderedDescending;
         }
         
         return NSOrderedAscending;
     }];
    
    for (NSString* rangeString in ranges)
    {
        CFTextAttachment* attachment    = [[CFTextAttachment alloc] init];
        attachment.bounds               = gifRect;
        attachment.gifName              = gifEomtionDict[rangeString];
        NSAttributedString* attachmentString = [NSAttributedString attributedStringWithAttachment:attachment];
        [attributedString replaceCharactersInRange:NSRangeFromString(rangeString) withAttributedString:attachmentString];
    }

    [attributedString addAttribute:NSFontAttributeName value:[UIFont fontWithName:@"Avenir" size:16] range:NSMakeRange(0, attributedString.length)];
    
    NSMutableParagraphStyle* paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    [paragraphStyle setParagraphStyle:[NSParagraphStyle defaultParagraphStyle]];
    paragraphStyle.lineBreakMode = NSLineBreakByCharWrapping;
    
    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, attributedString.length)];
    
    self.attributedString = attributedString;
}

- (void)setAttributedString:(NSMutableAttributedString *)attributedString
{
    _attributedString = attributedString;
    self.height = [attributedString boundingRectWithSize:CGSizeMake([UIScreen mainScreen].bounds.size.width-40 , CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesDeviceMetrics|NSStringDrawingTruncatesLastVisibleLine context:NULL].size.height+20;
    NSLog(@"heigt = %f", self.height);
}

@end
