/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.SharedPsiElementImplUtil;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Evgeny Gerashchenko
 * @since 3/2/12
 */
public class JetClassFileViewProvider2 extends UserDataHolderBase implements FileViewProvider {
    private String myText;
    private final PsiManager myManager;
    private PsiFile myPsiFile = null;
    private final VirtualFile myVirtualFile;

    public JetClassFileViewProvider2(@NotNull PsiManager manager, @NotNull VirtualFile file, String text) {
        myManager = manager;
        myVirtualFile = file;
        myText = text;
    }

    @Override
    @NotNull
    public PsiManager getManager() {
        return myManager;
    }

    @Override
    @Nullable
    public Document getDocument() {
        return null;
    }

    @Override
    @NotNull
    public CharSequence getContents() {
        return myText;
    }

    @Override
    @NotNull
    public VirtualFile getVirtualFile() {
        return myVirtualFile;
    }

    @Override
    @NotNull
    public Language getBaseLanguage() {
        return JetLanguage.INSTANCE;
    }

    @Override
    @NotNull
    public Set<Language> getLanguages() {
        return Collections.singleton(getBaseLanguage());
    }

    @Override
    public PsiFile getPsi(@NotNull Language target) {
        if (myPsiFile == null) {
            final ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(JetLanguage.INSTANCE);
            if (parserDefinition != null) {
                myPsiFile = parserDefinition.createFile(this);
            }
        }
        return myPsiFile;
    }

    @Override
    @NotNull
    public List<PsiFile> getAllFiles() {
        return Collections.singletonList(getPsi(getBaseLanguage()));
    }

    @Override
    public void beforeContentsSynchronized() {}

    @Override
    public void contentsSynchronized() {}

    @Override
    public boolean isEventSystemEnabled() {
        return true;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @Override
    public boolean supportsIncrementalReparse(@NotNull final Language rootLanguage) {
        return true;
    }

    @Override
    public void rootChanged(@NotNull PsiFile psiFile) {
    }

    @Override
    public FileViewProvider clone(){
        throw new RuntimeException("Clone is not supported for DummyHolderProviders. Use DummyHolder clone directly.");
    }

    @Override
    public PsiReference findReferenceAt(final int offset) {
        return SharedPsiElementImplUtil.findReferenceAt(getPsi(getBaseLanguage()), offset);
    }

    @Override
    @Nullable
    public PsiElement findElementAt(final int offset, @NotNull final Language language) {
        return language == getBaseLanguage() ? findElementAt(offset) : null;
    }


    @Override
    public PsiElement findElementAt(int offset, @NotNull Class<? extends Language> lang) {
        if (!lang.isAssignableFrom(getBaseLanguage().getClass())) return null;
        return findElementAt(offset);
    }

    @Override
    public PsiReference findReferenceAt(final int offsetInElement, @NotNull final Language language) {
        return language == getBaseLanguage() ? findReferenceAt(offsetInElement) : null;
    }

    @NotNull
    @Override
    public FileViewProvider createCopy(@NotNull final VirtualFile copy) {
        throw new RuntimeException("Clone is not supported for DummyHolderProviders. Use DummyHolder clone directly.");
    }

    @Override
    public PsiElement findElementAt(final int offset) {
        final LeafElement element = ((PsiFileImpl)getPsi(getBaseLanguage())).calcTreeElement().findLeafElementAt(offset);
        return element != null ? element.getPsi() : null;
    }
}
