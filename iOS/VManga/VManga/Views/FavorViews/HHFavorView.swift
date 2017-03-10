//
//  HHFavorView.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHFavorView: UIView {

    /*
    // Only override draw() if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func draw(_ rect: CGRect) {
        // Drawing code
    }
    */
    @IBOutlet var EmptyFavorBookView: UIView!
    @IBOutlet var favorBookView: UIView!
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
//        self.EmptyFavorBookView?.backgroundColor = UIColor(red: 255/255.0, green: 255/255.0, blue: 255/255.0, alpha: 1.0)
        //self.favorBookView.backgroundColor = viewBackgroundColor
        UINib.init(nibName: "HHFavorView", bundle: nil).instantiate(withOwner: self, options: nil)
        self.addSubview(EmptyFavorBookView)
        EmptyFavorBookView.frame = self.bounds
    }
}
