//
//  SignViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/18/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit

class SignViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onTappedLoginButton(_ sender: Any) {
        performSegue(withIdentifier: StorySegues.FromSignToLogin.rawValue, sender: self)
    }
    
    @IBAction func onTappedSignupButton(_ sender: Any) {
        performSegue(withIdentifier: StorySegues.FromSignToSignup.rawValue, sender: self)
    }

}
