//
//  MainTabBarViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import SimpleTabBarController


class MainTabBarViewController: SimpleTabBarController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        selectTab(index: 0)
    }
    
    func selectTab(index: Int) {
        self.selectedIndex = index
    }

}
