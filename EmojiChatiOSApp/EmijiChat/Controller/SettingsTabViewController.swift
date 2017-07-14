//
//  SettingsViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit

class SettingsTabViewController: UIViewController {

    
    //@IBOutlet weak var scrollView: UIScrollView!
    //@IBOutlet weak var contentView: UIView!
    @IBOutlet weak var userImageView: UIImageView!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        userImageView.layer.borderColor = UIColor.white.cgColor
//        scrollView.delegate = self
        // Do any additional setup after loading the view.
    }

    override func viewDidLayoutSubviews()
    {
//        let scrollViewBounds = scrollView.bounds
//        let containerViewBounds = contentView.bounds
//        
//        scrollView.contentSize = CGSize(width: scrollViewBounds.size.width, height: containerViewBounds.size.height)
//        scrollView.isScrollEnabled = true
//        scrollView.isDirectionalLockEnabled = true
        
        /*var scrollViewInsets =   UIEdgeInsetsZero
        scrollViewInsets.top = scrollViewBounds.size.height/2.0;
        scrollViewInsets.top -= contentView.bounds.size.height/2.0;
        
        scrollViewInsets.bottom = scrollViewBounds.size.height/2.0
        scrollViewInsets.bottom -= contentView.bounds.size.height/2.0;
        scrollViewInsets.bottom += 1*/
        
        //scrollView.contentInset = scrollViewInsets
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
//    func scrollViewDidScroll(scrollView: UIScrollView) {
//        if scrollView.contentOffset.x>0 {
//            scrollView.contentOffset.x = 0
//        }
//    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
