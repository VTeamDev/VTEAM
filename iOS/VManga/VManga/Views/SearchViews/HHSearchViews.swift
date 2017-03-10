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
   // @IBOutlet var view: UIView!
    @IBOutlet var view: HHSearchViews!

    
    @IBOutlet weak var searchText: UISearchBar!

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
     
       UINib.init(nibName: "HHSearchViews", bundle: nil).instantiate(withOwner: self, options: nil)
        self.addSubview(view)
        view.frame = self.bounds
        
    }
}
