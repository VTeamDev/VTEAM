//
//  HHReadingViewController.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit
import SKPhotoBrowser
import SDWebImage
import PromiseKit

class HHReadingViewController: UIViewController  {
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    var images = [SKPhotoProtocol]()

    private func getPages() -> Promise<[SKPhotoProtocol]> {
        return Promise { resolve, reject in
            API.getChapter(manga_id: 11909, chapterId: 0).then { pages -> Void in
                let skPages = pages.map({SKPhoto.photoWithImageURL($0)})
                resolve(skPages)
                }.catch { e in reject(e) }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        SKCache.sharedCache.imageCache = CustomImageCache()
        
        SKPhotoBrowserOptions.displayAction = false
        SKPhotoBrowserOptions.backgroundColor = colors.background
        
        getPages().then { pages -> Void in
            self.activityIndicator.stopAnimating()
            
            let browser = SKPhotoBrowser(photos: pages)
            browser.initializePageIndex(0)
            browser.delegate = self
            self.present(browser, animated: true, completion: nil)
        }.catch { e in }
    }
}

// MARK: - SKPhotoBrowserDelegate

extension HHReadingViewController: SKPhotoBrowserDelegate {
    func didDismissAtPageIndex(_ index: Int) {
    }
    
    func didDismissActionSheetWithButtonIndex(_ buttonIndex: Int, photoIndex: Int) {
    }
    
    func removePhoto(index: Int, reload: (() -> Void)) {
        SKCache.sharedCache.removeImageForKey("somekey")
        reload()
    }
}

class CustomImageCache: SKImageCacheable {
    var cache: SDImageCache
    
    init() {
        let cache = SDImageCache(namespace: "com.long.custom.cache")
        self.cache = cache
    }
    
    func imageForKey(_ key: String) -> UIImage? {
        guard let image = cache.imageFromDiskCache(forKey: key) else { return nil }
        
        return image
    }
    
    func setImage(_ image: UIImage, forKey key: String) {
        cache.store(image, forKey: key)
    }
    
    func removeImageForKey(_ key: String) {
    }
}
