/*
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary")

import io.verik.core.*

class env_config : Class {

    val class_bfm: tinyalu_bfm
    val module_bfm: tinyalu_bfm

    constructor(class_bfm: tinyalu_bfm, module_bfm: tinyalu_bfm) : super() {
        this.class_bfm = class_bfm
        this.module_bfm = module_bfm
    }
}
