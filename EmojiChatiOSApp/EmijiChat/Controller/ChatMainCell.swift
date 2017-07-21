//
//  ChatMainCell.swift
//  EmijiChat
//
//  Created by LEE on 6/20/17.
//  Copyright © 2017 Meapp90. All rights reserved.
//

import Foundation
import UIKit
import AsyncImageView


class ChatMainCell: UITableViewCell {
   
    @IBOutlet weak var imageForUser: AsyncImageView!
    @IBOutlet weak var labelForUsername: UILabel!
    @IBOutlet weak var textviewForMessage: CFTextView!
    @IBOutlet weak var viewForBadge: UIView!
    @IBOutlet weak var labelForBadge: UILabel!
    @IBOutlet weak var labelForTime: UILabel!
    
}
