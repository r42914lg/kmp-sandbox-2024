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
    var picState: PictureViewState = .Loading.shared

    var breedName: String = ""

    init(breed: String) {
        self.breedName = breed
    }

    var body: some View {
        ZStack {
            VStack {
                switch onEnum(of: picState) {
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
        .task {
            let viewModel = KotlinDependencies.shared.getPictureViewModel(breed: breedName)
            await withTaskCancellationHandler(
                operation: {
                    self.viewModel = viewModel
                    Task {
                        try? await viewModel.activate()
                    }
                    for await state in viewModel.state {
                        self.picState = state
                    }
                },
                onCancel: {
                    viewModel.clear()
                    self.viewModel = nil
                }
            )
        }
    }
}
