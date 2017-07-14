//
//  Utils.swift
//  Onefortheride
//
//  Created by mac on 01/12/15.
//  Copyright © 2015 mac. All rights reserved.
//

import Foundation
import UIKit

struct Utils {
    static func maskImage(image: UIImage, maskImage: UIImage) -> UIImage {
        let maskRef = maskImage.cgImage
        let mask = CGImage(maskWidth: maskRef!.width, height: maskRef!.height, bitsPerComponent: maskRef!.bitsPerComponent, bitsPerPixel: maskRef!.bitsPerPixel, bytesPerRow: maskRef!.bytesPerRow, provider: maskRef!.dataProvider!, decode: nil, shouldInterpolate: false)
        let masked = image.cgImage!.masking(mask!)
        
        return UIImage(cgImage: masked!)
    }
    
    /*func imageWithImage(image: UIImage, newSize: CGSize) -> UIImage{
        UIGraphicsBeginImageContext(newSize)
        image.draw(in: CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height))
        let newImage: UIImage = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        return newImage
    }*/
    
    static func imageWithImage(image: UIImage, newSize: CGSize) -> UIImage {
        let squareImage = getSquareImage(image: image)
        
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        squareImage.draw(in: CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage!
    }
    
    static func getSquareImage(image: UIImage) -> UIImage {
        var fromRect: CGRect?
        if (image.size.width > image.size.height) {
            
            
            fromRect = CGRect(x: (image.size.width - image.size.height) / 2, y: 0, width: image.size.height, height: image.size.height)
        } else {
            fromRect = CGRect(x: 0, y: (image.size.height - image.size.width) / 2, width: image.size.width, height: image.size.width)
        }
        
        let drawImage = image.cgImage!.cropping(to: fromRect!)
        let newImage = UIImage.init(cgImage: drawImage!)
        
        return newImage
    }
    
    static func profileImage(image: UIImage) -> UIImage {
        let size = CGSize(width: image.size.width, height: image.size.height)
        let maskImg = Utils.imageWithImage(image: UIImage(named: "profilepic-mask")!, newSize: size)
        
        return Utils.maskImage(image: image, maskImage: maskImg)
    }
    
    static func fixOrientation(image: UIImage) -> UIImage {
        // No-op if the orientation is already correct
        if (image.imageOrientation == .up) {
            return image
        }
        
        // We need to calculate the proper transformation to make the image upright.
        // We do it in 2 steps: Rotate if Left/Right/Down, and then flip if Mirrored.
        
        //var transform = CGAffineTransformIdentity
        var transform = CGAffineTransform.identity
        
        switch (image.imageOrientation) {
        case .down:
            transform = transform.translatedBy(x: image.size.width, y: image.size.height)
            transform = transform.rotated(by: CGFloat(M_PI))
            break
        case .downMirrored:
            transform = transform.translatedBy(x: image.size.width, y: image.size.height)
            transform = transform.rotated(by: CGFloat(M_PI))
            break
            
        case .left:
            transform = transform.translatedBy(x: image.size.width, y: 0)
            transform = transform.rotated(by: CGFloat(M_PI_2))
            break
        case .leftMirrored:
            transform = transform.translatedBy(x: image.size.width, y: 0)
            transform = transform.rotated(by: CGFloat(M_PI_2))
            break
            
        case .right:
            transform = transform.translatedBy(x: 0, y: image.size.height)
            transform = transform.rotated(by: CGFloat(-M_PI_2))
            break
        case .rightMirrored:
            transform = transform.translatedBy(x: 0, y: image.size.height)
            transform = transform.rotated(by: CGFloat(-M_PI_2))
            break
            
        case .up:
            break
        case .upMirrored:
            break;
        }
        
        switch (image.imageOrientation) {
        case .upMirrored:
            transform = transform.translatedBy(x: image.size.width, y: 0)
            transform = transform.scaledBy(x: -1, y: 1)
            break
        case .downMirrored:
            transform = transform.translatedBy(x: image.size.width, y: 0)
            transform = transform.scaledBy(x: -1, y: 1)
            break
            
        case .leftMirrored:
            transform = transform.translatedBy(x: image.size.height, y: 0)
            transform = transform.scaledBy(x: -1, y: 1)
            break
        case .rightMirrored:
            transform = transform.translatedBy(x: image.size.height, y: 0)
            transform = transform.scaledBy(x: -1, y: 1)
            break
            
        case .up:
            break
        case .down:
            break
        case .left:
            break
        case .right:
            break
        }
        
        // Now we draw the underlying CGImage into a new context, applying the transform
        // calculated above.
        let ctx = CGContext(data: nil, width: Int(image.size.width), height: Int(image.size.height),
                            bitsPerComponent: image.cgImage!.bitsPerComponent, bytesPerRow: 0,
                            space: image.cgImage!.colorSpace!,
                            bitmapInfo: image.cgImage!.bitmapInfo.rawValue)
        ctx!.concatenate(transform)
        
        switch (image.imageOrientation) {
        case .left:
            //CGContextDrawImage(ctx, CGRect(x: 0,y: 0, width: image.size.height, height: image.size.width), image.cgImage);
            ctx?.draw(image.cgImage!, in: CGRect(x: 0.0,y: 0.0,width: image.size.height,height: image.size.width))
            
            break;
        case .leftMirrored:
            //CGContextDrawImage(ctx, CGRect(X: 0, y: 0, width: image.size.height, height: image.size.width), image.cgImage);
            ctx?.draw(image.cgImage!, in: CGRect(x: 0.0,y: 0.0,width: image.size.height,height: image.size.width))
            break;
        case .right:
            //CGContextDrawImage(ctx, CGRect(x: 0, y: 0, width: image.size.height, height: image.size.width), image.cgImage);
            ctx?.draw(image.cgImage!, in: CGRect(x: 0.0,y: 0.0,width: image.size.height,height: image.size.width))
            break;
        case .rightMirrored:
            // Grr...
            //CGContextDrawImage(ctx, CGRect(x: 0, y: 0, width: image.size.height, height: image.size.width), image.cgImage);
            ctx?.draw(image.cgImage!, in: CGRect(x: 0.0,y: 0.0,width: image.size.height,height: image.size.width))
            break;
            
        default:
            //CGContextDrawImage(ctx, CGRect(x: 0, y: 0, width: image.size.width, height: image.size.height), image.cgImage);
            ctx?.draw(image.cgImage!, in: CGRect(x: 0.0,y: 0.0,width: image.size.width, height: image.size.height))
            break;
        }
        
        // And now we just create a new UIImage from the drawing context
        let cgimg = ctx!.makeImage()
        let img = UIImage.init(cgImage: cgimg!)
        
        return img
    }
    
    static func getStringFromDate(_date: NSDate?) -> String {
        if (_date == nil) {
            return ""
        }
        
        let date = NSDate()
        var dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        let str = dateFormatter.string(from: date as Date)
        
        
        /*let calendar = NSCalendar.current
         let components = calendar.components([.Year, .Month, .Day], fromDate: _date!)
         let str = String(format: "%.2d/%.2d/%d", components.month, components.day, components.year)*/
        
        return str
    }
}
