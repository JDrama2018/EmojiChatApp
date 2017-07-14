//
//  ChatTabViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import GoogleMobileAds

class ChatTabViewController: UIViewController, GADBannerViewDelegate {
    
    @IBOutlet weak var GoogleBannerView: GADBannerView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        initAdMobBanner()
    }
    
    func initAdMobBanner() {
        
        if UIDevice.current.userInterfaceIdiom == .phone {
            // iPhone
            GoogleBannerView.adSize =  GADAdSizeFromCGSize(CGSize(width: 320, height: 50))
            GoogleBannerView.frame = CGRect(x: 0, y: view.frame.size.height, width: 320, height: 50)
        } else  {
            // iPad
            GoogleBannerView.adSize =  GADAdSizeFromCGSize(CGSize(width: 468, height: 60))
            GoogleBannerView.frame = CGRect(x: 0, y: view.frame.size.height, width: 468, height: 60)
        }
        
        GoogleBannerView.adUnitID = "ca-app-pub-8501671653071605/1974659335"    //"ca-app-pub-8501671653071605/1974659335"    //"ca-app-pub-3535011541485588/6026884358" //ADMOB_BANNER_UNIT_ID
        GoogleBannerView.rootViewController = self
        GoogleBannerView.delegate = self
        
        let request = GADRequest()
        GoogleBannerView.load(request)
        
    }
    
    func adViewDidReceiveAd(_ bannerView: GADBannerView!) {
        print("Banner loaded successfully")
    }
    
    func adView(_ bannerView: GADBannerView!, didFailToReceiveAdWithError error: GADRequestError!) {
        print("Fail to receive ads")
        print(error)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}
