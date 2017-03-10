//
//  HHSearchViews.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHSearchViews: UIView {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
    @IBOutlet var view: UIView!

    @IBOutlet weak var searchField: UITextField!
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
//        let imageView = UIImageView()
//        let image = UIImage(named: "SearchField")
//        imageView.image = image
//        
//        imageView.frame = CGRect(x: 0, y: 0, width: 100, height: 200)
//        view.addSubview(imageView)
//        
//        
//        searchField.leftView = imageView
//        searchField.leftViewMode = UITextFieldViewMode.always
        
    }
}
