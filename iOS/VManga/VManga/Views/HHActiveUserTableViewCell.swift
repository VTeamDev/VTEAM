//
//  HHTableViewCell.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/12/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit

class HHActiveUserTableViewCell: UITableViewCell {
    @IBOutlet weak var avatar: UIImageView!
    @IBOutlet weak var statusLabel: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
