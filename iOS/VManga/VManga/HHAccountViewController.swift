//
//  HHAccountViewController.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit
import FacebookLogin
import FacebookCore

class HHAccountViewController: UIViewController {
    
    private func addLoginButton() {
        let loginButton = LoginButton(readPermissions: [ .publicProfile, .email, .userFriends ])
        loginButton.center = view.center
        loginButton.delegate = self
        view.addSubview(loginButton)
        
        if let accessToken = AccessToken.current {
            API.testFacebook(token: accessToken.authenticationToken)
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        addLoginButton()
    }

}

extension HHAccountViewController: LoginButtonDelegate {
    func loginButtonDidCompleteLogin(_ loginButton: LoginButton, result: LoginResult) {
        if let accessToken = AccessToken.current {
            API.testFacebook(token: accessToken.authenticationToken)
        }
    }
    
    func loginButtonDidLogOut(_ loginButton: LoginButton) {
        
    }
}
