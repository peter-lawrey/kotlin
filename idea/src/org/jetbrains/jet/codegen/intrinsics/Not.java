package org.jetbrains.jet.codegen.intrinsics;

import com.intellij.psi.PsiElement;
import org.jetbrains.jet.codegen.ExpressionCodegen;
import org.jetbrains.jet.codegen.StackValue;
import org.jetbrains.jet.lang.psi.JetExpression;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;

/**
 * @author yole
 */
public class Not implements IntrinsicMethod {
    @Override
    public StackValue generate(ExpressionCodegen codegen, InstructionAdapter v, Type expectedType, PsiElement element, List<JetExpression> arguments, boolean haveReceiver) {
        final StackValue stackValue;
        if (arguments.size() == 1) {
            stackValue = codegen.generateIntermediateValue(arguments.get(0));
        }
        else {
            stackValue = codegen.getReceiverAsStackValue(element, null, expectedType);
        }
        return StackValue.not(stackValue);
    }
}