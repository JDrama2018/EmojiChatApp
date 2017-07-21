//
//  ProfileController.swift
//  EmijiChat
//
//  Created by Nikita Rodionov on 11.07.17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import UIKit

class ProfileController: UIViewController,UIImagePickerControllerDelegate,UINavigationControllerDelegate{

    @IBOutlet weak var userImageView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        userImageView.layer.borderColor = UIColor.white.cgColor
    }

    @IBAction func changePhotoTapped(_ sender: Any) {
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = .photoLibrary;
            imagePicker.allowsEditing = true
            present(imagePicker, animated: true, completion: nil)
        }
    }
    
    @IBAction func backTapped(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        let chosenImage = info[UIImagePickerControllerOriginalImage] as! UIImage
        userImageView.image = chosenImage
        dismiss(animated: true, completion: nil)
    }
    
}
