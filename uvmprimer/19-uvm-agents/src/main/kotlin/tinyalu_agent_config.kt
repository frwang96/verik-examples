/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Verik
@file:Suppress("ClassName", "ConvertSecondaryConstructorToPrimary", "FunctionName")

import imported.uvm_pkg.uvm_active_passive_enum
import io.verik.core.*

class tinyalu_agent_config : Class {

    val bfm: tinyalu_bfm
    private val is_active: uvm_active_passive_enum

    constructor(bfm: tinyalu_bfm, is_active: uvm_active_passive_enum) : super() {
        this.bfm = bfm
        this.is_active = is_active
    }

    fun get_is_active(): uvm_active_passive_enum {
        return is_active
    }
}
