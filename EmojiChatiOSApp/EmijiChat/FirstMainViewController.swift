//
//  ViewController.swift
//  EmijiChat
//
//  Created by LEE on 6/17/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit
import FirebaseAuth


class FirstMainViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func onTappedContinueButton(_ sender: Any) {
        
        // Authentication ====================================================================
        FIRSignin(completion: {
            self.performSegue(withIdentifier: StorySegues.FromFirstMainToSign.rawValue, sender: self)
        })
    }

    
    
    
    // Firebase Signup using email
/*    func AuenticationFirebase(completion: @escaping() -> Void) {
        FIRSignup(completion: {
            
            self.FIRSignin(completion: {
                print("ok - AuenticationFirebase")
            })
        })
    }
    
    func FIRSignup(completion: @escaping() -> Void) {
        FIRAuth.auth()?.createUser(withEmail: "elizhang221@gmail.com", password: "elizhang221") { (user, error) in
            
            if error == nil {
                
                DispatchQueue.main.async {
                    print("ok - Signup")
                    completion()
                }
            }
            else
            {
                print("ok - Signin")
                completion()
            }
        }
    }
*/
    func FIRSignin(completion: @escaping() -> Void) {
        
        ProgressHUD.show("Loading...")
        FIRAuth.auth()?.signIn(withEmail: "elizhang221@gmail.com", password: "elizhang221") { (user, error) in
            
            if error == nil {
                
                ProgressHUD.dismiss()
                completion()
            } else {
                ProgressHUD.dismiss()
                self.view.makeToast("Network is busy now.", duration: 2.0, position: .bottom)
            }
        }
    }
    
    @IBAction func onTappedTerms(_ sender: Any) {
        UIApplication.shared.openURL(NSURL(string: "http://www.MuslimEmoji.com/Terms")! as URL)
    }
    
    @IBAction func onTappedPrivacy(_ sender: Any) {
        UIApplication.shared.openURL(NSURL(string: "http://www.MuslimEmoji.com/PrivacyPolicy")! as URL)
    }
}

