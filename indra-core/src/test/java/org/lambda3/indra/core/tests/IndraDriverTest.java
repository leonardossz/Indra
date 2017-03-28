package org.lambda3.indra.core.tests;

import org.apache.commons.math3.linear.RealVector;
import org.lambda3.indra.client.ScoreFunction;
import org.lambda3.indra.core.*;
import org.lambda3.indra.core.composition.VectorComposition;
import org.lambda3.indra.core.translation.IndraTranslatorFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*-
 * ==========================License-Start=============================
 * Indra Core Module
 * --------------------------------------------------------------------
 * Copyright (C) 2016 - 2017 Lambda^3
 * --------------------------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ==========================License-End===============================
 */
public class IndraDriverTest {

    private IndraDriver driver;

    public IndraDriverTest() {
        Params params = new Params("", ScoreFunction.COSINE, "PT", "", true, VectorComposition.SUM, VectorComposition.AVERAGE);
        VectorSpaceFactory vectorSpaceFactory = createVectorSpaceFactor();
        IndraTranslatorFactory translatorFactory = createIndraTranslatorFactory();
        this.driver = new IndraDriver(params, vectorSpaceFactory, translatorFactory) {
        };
    }

    public VectorSpaceFactory createVectorSpaceFactor() {
        VectorSpaceFactory factory = new VectorSpaceFactory() {
            @Override
            protected VectorSpace doCreate(Params params) {
                VectorComposerFactory composerFactory = new VectorComposerFactory();
                return new MockCachedVectorSpace(composerFactory.getComposer(params.termComposition),
                        composerFactory.getComposer(params.translationComposition));
            }

            @Override
            protected Object createKey(Params params) {
                return params;
            }
        };

        return factory;
    }

    public IndraTranslatorFactory createIndraTranslatorFactory() {
        IndraTranslatorFactory factory = new IndraTranslatorFactory() {
            @Override
            protected Object doCreate(Params params) {
                return new MockIndraTranslator();
            }

            @Override
            protected Object createKey(Params params) {
                return params;
            }
        };

        return factory;
    }

    @Test
    public void getTranslatedVectors() {
        List<String> terms = Arrays.asList("mãe", "pai");
        Map<String, RealVector> res = driver.getVectors(terms);
        Assert.assertEquals(res.get("mãe"), MockCachedVectorSpace.ONE_VECTOR);
        Assert.assertEquals(res.get("paigit"), MockCachedVectorSpace.NEGATIVE_ONE_VECTOR);
    }
}