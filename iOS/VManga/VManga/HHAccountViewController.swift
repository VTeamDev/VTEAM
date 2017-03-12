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
    
    func setCurrentUser() {
        if let accessToken = AccessToken.current {
            API.getUserWithFbToken(token: accessToken.authenticationToken).then { user -> Void in
                User.current = user
            }.catch { e in }
        }
    }
    
    private func addLoginButton() {
        let loginButton = LoginButton(readPermissions: [ .publicProfile, .email, .userFriends ])
        loginButton.center = view.center
        loginButton.delegate = self
        view.addSubview(loginButton)
        
        setCurrentUser()
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        addLoginButton()
    }

}

extension HHAccountViewController: LoginButtonDelegate {
    func loginButtonDidCompleteLogin(_ loginButton: LoginButton, result: LoginResult) {
        setCurrentUser()
    }
    
    func loginButtonDidLogOut(_ loginButton: LoginButton) {
        
    }
}
