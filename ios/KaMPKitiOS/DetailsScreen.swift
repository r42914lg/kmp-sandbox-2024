//
//  DetailsScreen.swift
//  KaMPKitiOS
//
//  Created by Leonid Gomberg on 04.03.2024.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import SwiftUI
import shared

struct DetailsScreen: View {

    @State
    var viewModel: PictureViewModel?

    @State
    var state: PictureViewState = .Loading.shared

    var body: some View {
        ZStack {
            VStack {
                switch onEnum(of: state) {
                case .content(let content):
                    Text(content.pictureUrl)
                case .error:
                    Spacer()
                    Text("Error while loading!!!")
                        .foregroundColor(.red)
                    Spacer()
                case .loading:
                    Spacer()
                    Text("Loading...")
                    Spacer()
                }
            }
        }
    }
}
