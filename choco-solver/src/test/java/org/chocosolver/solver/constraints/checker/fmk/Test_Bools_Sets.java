/**
 * Copyright (c) 2015, Ecole des Mines de Nantes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chocosolver.solver.constraints.checker.fmk;

import org.testng.annotations.Test;


/**
 * @author Jean-Guillaume Fages
 * @since 01/13
 */
public class Test_Bools_Sets {

    public Test_Bools_Sets(){
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test(groups="10s", timeOut=60000)
    public void testBOOL_SUM() {
        for (int i = 0; i < 10; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.boolSum, n, -n / 2, 2 * n, seed, null);
            }
        }
    }

    @Test(groups="5m", timeOut=300000)
    public void setUnion() {
        for (int i = 0; i < 5; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.setUnion, n, -n / 2, 2 * n, seed, null);
            }
        }
    }

    @Test(groups="5m", timeOut=300000)
    public void setInter() {
        for (int i = 0; i < 5; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.setInter, n, -n / 2, 2 * n, seed, null);
            }
        }
    }

    @Test(groups="5m", timeOut=300000)
    public void setDisj() {
        for (int i = 0; i < 5; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.setDisj, n, -n / 2, 2 * n, seed, null);
            }
        }
    }

    @Test(groups="5m", timeOut=300000)
    public void setDiff() {
        for (int i = 0; i < 5; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.setDiff, n, -n / 2, 2 * n, seed, null);
            }
        }
    }

    @Test(groups="5m", timeOut=300000)
    public void setSubSet() {
        for (int i = 0; i < 5; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.setSubSet, n, -n / 2, 2 * n, seed, null);
            }
        }
    }

    @Test(groups="5m", timeOut=300000)
    public void setAllEq() {
        for (int i = 0; i < 5; i++) {
            long seed = System.currentTimeMillis();
            for (int n = 2; n < (1 << 5) + 1; n *= 2) {
                Correctness.checkCorrectness(SetTestModel.setAllEq, n, -n / 2, 2 * n, seed, null);
            }
        }
    }
}
