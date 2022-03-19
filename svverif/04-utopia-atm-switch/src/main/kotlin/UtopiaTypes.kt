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

import io.verik.core.*

class UniCell(
    var gfc: Ubit<`4`>,
    var vpi: Ubit<`8`>,
    var vci: Ubit<`16`>,
    var clp: Boolean,
    var pt: Ubit<`3`>,
    var hec: Ubit<`8`>,
    var payload: Packed<`48`, Ubit<`8`>>
) : Struct()

class NniCell(
    var vpi: Ubit<`12`>,
    var vci: Ubit<`16`>,
    var clp: Boolean,
    var pt: Ubit<`3`>,
    var hec: Ubit<`8`>,
    var payload: Packed<`48`, Ubit<`8`>>
) : Struct()

class TestCell(
    var header: Packed<`5`, Ubit<`8`>>,
    var port_id: Packed<`4`, Ubit<`8`>>,
    var cell_id: Packed<`4`, Ubit<`8`>>,
    var padding: Packed<`40`, Ubit<`8`>>
) : Struct()

class AtmCell(
    var uni: UniCell = nc(),
    var nni: NniCell = nc(),
    var test: TestCell = nc(),
    var mem: Packed<`53`, Ubit<`8`>> = nc()
) : Union()

class CellConfig(
    var fwd: Ubit<TX_PORTS>,
    var vpi: Ubit<`12`>
) : Struct()
