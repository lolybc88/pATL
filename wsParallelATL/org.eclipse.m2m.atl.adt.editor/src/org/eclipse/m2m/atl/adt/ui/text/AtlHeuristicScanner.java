/*******************************************************************************
 * Copyright (c) 2004 INRIA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    INRIA - initial API and implementation
 *******************************************************************************/
package org.eclipse.m2m.atl.adt.ui.text;

import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;

/**
 * Utility methods for heuristic based manipulations in an incomplete source file.
 * <p>
 * An instance holds some internal position in the document and is therefore not threadsafe.
 * </p>
 */
public class AtlHeuristicScanner implements Symbols {

	/**
	 * Stops upon a character in the default partition that matches the given character list.
	 */
	private class CharacterMatch implements StopCondition {
		private final char[] fChars;

		/**
		 * Creates a new instance.
		 * 
		 * @param ch
		 *            the single character to match
		 */
		public CharacterMatch(char ch) {
			this(new char[] {ch});
		}

		/**
		 * Creates a new instance.
		 * 
		 * @param chars
		 *            the chars to match.
		 */
		public CharacterMatch(char[] chars) {
			Assert.isNotNull(chars);
			Assert.isTrue(chars.length > 0);
			fChars = chars;
			Arrays.sort(chars);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.AtlHeuristicScanner.StopCondition#stop(char, int)
		 */
		public boolean stop(char ch, int position, boolean forward) {
			return Arrays.binarySearch(fChars, ch) >= 0 && isDefaultPartition(position);
		}
	}

	/**
	 * Stops upon a non-Atl identifier (as defined by {@link Character#isAtlIdentifierPart(char)}) character.
	 */
	private static class NonAtlIdentifierPart implements StopCondition {
		/*
		 * @see org.eclipse.jdt.internal.ui.text.AtlHeuristicScanner.StopCondition#stop(char)
		 */
		public boolean stop(char ch, int position, boolean forward) {
			return !Character.isJavaIdentifierPart(ch);
		}
	}

	/**
	 * Stops upon a non-Atl identifier character in the default partition.
	 * 
	 * @see NonAtlIdentifierPart
	 */
	private class NonAtlIdentifierPartDefaultPartition extends NonAtlIdentifierPart {
		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.m2m.atl.adt.ui.text.AtlHeuristicScanner.NonAtlIdentifierPart#stop(char, int, boolean)
		 */
		@Override
		public boolean stop(char ch, int position, boolean forward) {
			return super.stop(ch, position, true) || !isDefaultPartition(position);
		}
	}

	/**
	 * Stops upon a non-whitespace (as defined by {@link Character#isWhitespace(char)}) character.
	 */
	private static class NonWhitespace implements StopCondition {
		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.m2m.atl.adt.ui.text.AtlHeuristicScanner.StopCondition#stop(char, int, boolean)
		 */
		public boolean stop(char ch, int position, boolean forward) {
			return !Character.isWhitespace(ch);
		}
	}

	/**
	 * Stops upon a non-whitespace character in the default partition.
	 * 
	 * @see NonWhitespace
	 */
	private class NonWhitespaceDefaultPartition extends NonWhitespace {
		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.m2m.atl.adt.ui.text.AtlHeuristicScanner.NonWhitespace#stop(char, int, boolean)
		 */
		@Override
		public boolean stop(char ch, int position, boolean forward) {
			return super.stop(ch, position, true) && isDefaultPartition(position);
		}
	}

	/**
	 * Acts like character match, but skips all scopes introduced by parenthesis, brackets, and braces.
	 */
	protected class SkippingScopeMatch extends CharacterMatch {
		private int fDepth = 0;

		private char fOpening, fClosing;

		/**
		 * Creates a new instance.
		 * 
		 * @param ch
		 *            the single character to match
		 */
		public SkippingScopeMatch(char ch) {
			super(ch);
		}

		/**
		 * Creates a new instance.
		 * 
		 * @param chars
		 *            the chars to match.
		 */
		public SkippingScopeMatch(char[] chars) {
			super(chars);
		}

		@Override
		public boolean stop(char ch, int position, boolean forward) {

			if (fDepth == 0 && super.stop(ch, position, true))
				return true;
			else if (ch == fOpening)
				fDepth++;
			else if (ch == fClosing) {
				fDepth--;
				if (fDepth == 0) {
					fOpening = 0;
					fClosing = 0;
				}
			} else if (fDepth == 0) {
				fDepth = 1;
				if (forward) {

					switch (ch) {
						case LBRACE:
							fOpening = LBRACE;
							fClosing = RBRACE;
							break;
						case LPAREN:
							fOpening = LPAREN;
							fClosing = RPAREN;
							break;
						case LBRACKET:
							fOpening = LBRACKET;
							fClosing = RBRACKET;
							break;
					}

				} else {
					switch (ch) {
						case RBRACE:
							fOpening = RBRACE;
							fClosing = LBRACE;
							break;
						case RPAREN:
							fOpening = RPAREN;
							fClosing = LPAREN;
							break;
						case RBRACKET:
							fOpening = RBRACKET;
							fClosing = LBRACKET;
							break;
					}

				}
			}

			return false;

		}

	}

	/**
	 * Specifies the stop condition, upon which the <code>scanXXX</code> methods will decide whether to keep
	 * scanning or not. This interface may implemented by clients.
	 */
	public interface StopCondition {
		/**
		 * Instructs the scanner to return the current position.
		 * 
		 * @param ch
		 *            the char at the current position
		 * @param position
		 *            the current position
		 * @param forward
		 *            the iteration direction
		 * @return <code>true</code> if the stop condition is met.
		 */
		boolean stop(char ch, int position, boolean forward);
	}

	private static final char COLON = ':';

	private static final char COMMA = ',';

	private static final char EQUAL = '=';

	private static final StopCondition FNONWS = new NonWhitespace();

	/* character constants */
	private static final char LBRACE = '{';

	private static final char LBRACKET = '[';

	private static final char LPAREN = '(';

	/**
	 * Returned by all methods when the requested position could not be found, or if a
	 * {@link BadLocationException} was thrown while scanning.
	 */
	public static final int NOT_FOUND = -1;

	private static final char QUESTIONMARK = '?';

	private static final char RBRACE = '}';

	private static final char RBRACKET = ']';

	private static final char RPAREN = ')';

	private static final char SEMICOLON = ';';

	/**
	 * Special bound parameter that means either -1 (backward scanning) or <code>fDocument.getLength()</code>
	 * (forward scanning).
	 */
	public static final int UNBOUND = -2;

	/* internal scan state */

	/** the most recently read character. */
	private char fChar;

	/** The document being scanned. */
	private IDocument fDocument;

	private final StopCondition fNonIdent = new NonAtlIdentifierPartDefaultPartition();

	/* preset stop conditions */
	private final StopCondition fNonWSDefaultPart = new NonWhitespaceDefaultPartition();

	/** The partition to scan in. */
	private String fPartition;

	/** The partitioning being used for scanning. */
	private String fPartitioning;

	/** the most recently read position. */
	private int fPos;

	/**
	 * Calls <code>this(document, IAtlPartitions.Atl_PARTITIONING, IDocument.DEFAULT_CONTENT_TYPE)</code>.
	 * 
	 * @param document
	 *            the document to scan.
	 */
	public AtlHeuristicScanner(IDocument document) {
		this(document, IAtlPartitions.PARTITIONING, IDocument.DEFAULT_CONTENT_TYPE);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param document
	 *            the document to scan
	 * @param partitioning
	 *            the partitioning to use for scanning
	 * @param partition
	 *            the partition to scan in
	 */
	public AtlHeuristicScanner(IDocument document, String partitioning, String partition) {
		Assert.isNotNull(document);
		Assert.isNotNull(partitioning);
		Assert.isNotNull(partition);
		fDocument = document;
		fPartitioning = partitioning;
		fPartition = partition;
	}

	/**
	 * Returns the position of the closing peer character (forward search). Any scopes introduced by opening
	 * peers are skipped. All peers accounted for must reside in the default partition.
	 * <p>
	 * Note that <code>start</code> must not point to the opening peer, but to the first character being
	 * searched.
	 * </p>
	 * 
	 * @param start
	 *            the start position
	 * @param openingPeer
	 *            the opening peer character (e.g. '{')
	 * @param closingPeer
	 *            the closing peer character (e.g. '}')
	 * @return the matching peer character position, or <code>NOT_FOUND</code>
	 */
	public int findClosingPeer(int start, final char openingPeer, final char closingPeer) {
		Assert.isNotNull(fDocument);
		Assert.isTrue(start >= 0);

		try {
			int depth = 1;
			start -= 1;
			while (true) {
				start = scanForward(start + 1, UNBOUND, new CharacterMatch(new char[] {openingPeer,
						closingPeer}));
				if (start == NOT_FOUND)
					return NOT_FOUND;

				if (fDocument.getChar(start) == openingPeer)
					depth++;
				else
					depth--;

				if (depth == 0)
					return start;
			}

		} catch (BadLocationException e) {
			return NOT_FOUND;
		}
	}

	/**
	 * Finds the highest position in <code>fDocument</code> such that the position is &lt;=
	 * <code>position</code> and &gt; <code>bound</code> and
	 * <code>Character.isWhitespace(fDocument.getChar(pos))</code> evaluates to <code>false</code> and the
	 * position is in the default partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &lt; <code>position</code>, or <code>UNBOUND</code>
	 * @return the highest position of a non-whitespace character in (<code>bound</code>,
	 *         <code>position</code>] that resides in a Atl partition, or <code>NOT_FOUND</code> if none can
	 *         be found
	 */
	public int findNonWhitespaceBackward(int position, int bound) {
		return scanBackward(position, bound, fNonWSDefaultPart);
	}

	/**
	 * Finds the smallest position in <code>fDocument</code> such that the position is &gt;=
	 * <code>position</code> and &lt; <code>bound</code> and
	 * <code>Character.isWhitespace(fDocument.getChar(pos))</code> evaluates to <code>false</code> and the
	 * position is in the default partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &gt; <code>position</code>, or <code>UNBOUND</code>
	 * @return the smallest position of a non-whitespace character in [<code>position</code>,
	 *         <code>bound</code>) that resides in a Atl partition, or <code>NOT_FOUND</code> if none can be
	 *         found
	 */
	public int findNonWhitespaceForward(int position, int bound) {
		return scanForward(position, bound, fNonWSDefaultPart);
	}

	/**
	 * Finds the smallest position in <code>fDocument</code> such that the position is &gt;=
	 * <code>position</code> and &lt; <code>bound</code> and
	 * <code>Character.isWhitespace(fDocument.getChar(pos))</code> evaluates to <code>false</code>.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &gt; <code>position</code>, or <code>UNBOUND</code>
	 * @return the smallest position of a non-whitespace character in [<code>position</code>,
	 *         <code>bound</code>), or <code>NOT_FOUND</code> if none can be found
	 */
	public int findNonWhitespaceForwardInAnyPartition(int position, int bound) {
		return scanForward(position, bound, FNONWS);
	}

	/**
	 * Returns the position of the opening peer character (backward search). Any scopes introduced by closing
	 * peers are skipped. All peers accounted for must reside in the default partition.
	 * <p>
	 * Note that <code>start</code> must not point to the closing peer, but to the first character being
	 * searched.
	 * </p>
	 * 
	 * @param start
	 *            the start position
	 * @param openingPeer
	 *            the opening peer character (e.g. '{')
	 * @param closingPeer
	 *            the closing peer character (e.g. '}')
	 * @return the matching peer character position, or <code>NOT_FOUND</code>
	 */
	public int findOpeningPeer(int start, char openingPeer, char closingPeer) {
		Assert.isTrue(start < fDocument.getLength());

		try {
			int depth = 1;
			start += 1;
			while (true) {
				start = scanBackward(start - 1, UNBOUND, new CharacterMatch(new char[] {openingPeer,
						closingPeer}));
				if (start == NOT_FOUND)
					return NOT_FOUND;

				if (fDocument.getChar(start) == closingPeer)
					depth++;
				else
					depth--;

				if (depth == 0)
					return start;
			}

		} catch (BadLocationException e) {
			return NOT_FOUND;
		}
	}

	/**
	 * Computes the surrounding block around <code>offset</code>. The search is started at the beginning of
	 * <code>offset</code>, i.e. an opening brace at <code>offset</code> will not be part of the surrounding
	 * block, but a closing brace will.
	 * 
	 * @param offset
	 *            the offset for which the surrounding block is computed
	 * @return a region describing the surrounding block, or <code>null</code> if none can be found
	 */
	public IRegion findSurroundingBlock(int offset) {
		if (offset < 1 || offset >= fDocument.getLength())
			return null;

		int begin = findOpeningPeer(offset - 1, LBRACE, RBRACE);
		int end = findClosingPeer(offset, LBRACE, RBRACE);
		if (begin == NOT_FOUND || end == NOT_FOUND)
			return null;
		return new Region(begin, end + 1 - begin);
	}

	/**
	 * Returns the most recent internal scan position.
	 * 
	 * @return the most recent internal scan position.
	 */
	public int getPosition() {
		return fPos;
	}

	/**
	 * Returns one of the keyword constants or <code>TOKEN_IDENT</code> for a scanned identifier.
	 * 
	 * @param s
	 *            a scanned identifier
	 * @return one of the constants defined in {@link Symbols}
	 */
	private int getToken(String s) {
		Assert.isNotNull(s);

		switch (s.length()) {
			case 2:
				if ("if".equals(s)) //$NON-NLS-1$
					return TOKEN_IF;
				if ("do".equals(s)) //$NON-NLS-1$
					return TOKEN_DO;
				break;
			case 3:
				if ("for".equals(s)) //$NON-NLS-1$
					return TOKEN_FOR;
				if ("try".equals(s)) //$NON-NLS-1$
					return TOKEN_TRY;
				if ("new".equals(s)) //$NON-NLS-1$
					return TOKEN_NEW;
				break;
			case 4:
				if ("case".equals(s)) //$NON-NLS-1$
					return TOKEN_CASE;
				if ("else".equals(s)) //$NON-NLS-1$
					return TOKEN_ELSE;
				if ("goto".equals(s)) //$NON-NLS-1$
					return TOKEN_GOTO;
				break;
			case 5:
				if ("break".equals(s)) //$NON-NLS-1$
					return TOKEN_BREAK;
				if ("catch".equals(s)) //$NON-NLS-1$
					return TOKEN_CATCH;
				if ("while".equals(s)) //$NON-NLS-1$
					return TOKEN_WHILE;
				break;
			case 6:
				if ("return".equals(s)) //$NON-NLS-1$
					return TOKEN_RETURN;
				if ("static".equals(s)) //$NON-NLS-1$
					return TOKEN_STATIC;
				if ("switch".equals(s)) //$NON-NLS-1$
					return TOKEN_SWITCH;
				break;
			case 7:
				if ("default".equals(s)) //$NON-NLS-1$
					return TOKEN_DEFAULT;
				if ("finally".equals(s)) //$NON-NLS-1$
					return TOKEN_FINALLY;
				break;
			case 12:
				if ("synchronized".equals(s)) //$NON-NLS-1$
					return TOKEN_SYNCHRONIZED;
				break;
		}
		return TOKEN_IDENT;
	}

	/**
	 * Checks if the line seems to be an open condition not followed by a block (i.e. an if, while, or for
	 * statement with just one following statement, see example below).
	 * 
	 * <pre>
	 * if (condition)
	 * 	doStuff();
	 * </pre>
	 * <p>
	 * Algorithm: if the last non-WS, non-Comment code on the line is an if (condition), while (condition),
	 * for( expression), do, else, and there is no statement after that
	 * </p>
	 * 
	 * @param position
	 *            the insert position of the new character
	 * @param bound
	 *            the lowest position to consider
	 * @return <code>true</code> if the code is a conditional statement or loop without a block,
	 *         <code>false</code> otherwise
	 */
	public boolean isBracelessBlockStart(int position, int bound) {
		if (position < 1)
			return false;

		switch (previousToken(position, bound)) {
			case TOKEN_DO:
			case TOKEN_ELSE:
				return true;
			case TOKEN_RPAREN:
				position = findOpeningPeer(fPos, LPAREN, RPAREN);
				if (position > 0) {
					switch (previousToken(position - 1, bound)) {
						case TOKEN_IF:
						case TOKEN_FOR:
						case TOKEN_WHILE:
							return true;
					}
				}
		}

		return false;
	}

	/**
	 * Checks whether <code>position</code> resides in a default (Atl) partition of <code>fDocument</code>.
	 * 
	 * @param position
	 *            the position to be checked
	 * @return <code>true</code> if <code>position</code> is in the default partition of
	 *         <code>fDocument</code>, <code>false</code> otherwise
	 */
	public boolean isDefaultPartition(int position) {
		Assert.isTrue(position >= 0);
		Assert.isTrue(position <= fDocument.getLength());

		try {
			ITypedRegion region = TextUtilities.getPartition(fDocument, fPartitioning, position, false);
			return region.getType().equals(fPartition);

		} catch (BadLocationException e) {
		}

		return false;
	}

	/**
	 * Returns the next token in forward direction, starting at <code>start</code>, and not extending further
	 * than <code>bound</code>. The return value is one of the constants defined in {@link Symbols}. After a
	 * call, {@link #getPosition()} will return the position just after the scanned token (i.e. the next
	 * position that will be scanned).
	 * 
	 * @param start
	 *            the first character position in the document to consider
	 * @param bound
	 *            the first position not to consider any more
	 * @return a constant from {@link Symbols} describing the next token
	 */
	public int nextToken(int start, int bound) {
		int pos = scanForward(start, bound, fNonWSDefaultPart);
		if (pos == NOT_FOUND)
			return TOKEN_EOF;

		fPos++;

		switch (fChar) {
			case LBRACE:
				return TOKEN_LBRACE;
			case RBRACE:
				return TOKEN_RBRACE;
			case LBRACKET:
				return TOKEN_LBRACKET;
			case RBRACKET:
				return TOKEN_RBRACKET;
			case LPAREN:
				return TOKEN_LPAREN;
			case RPAREN:
				return TOKEN_RPAREN;
			case SEMICOLON:
				return TOKEN_SEMICOLON;
			case COMMA:
				return TOKEN_COMMA;
			case QUESTIONMARK:
				return TOKEN_QUESTIONMARK;
			case EQUAL:
				return TOKEN_EQUAL;
		}

		// else
		if (Character.isJavaIdentifierPart(fChar)) {
			// assume an ident or keyword
			int from = pos, to;
			pos = scanForward(pos + 1, bound, fNonIdent);
			if (pos == NOT_FOUND)
				to = bound == UNBOUND ? fDocument.getLength() : bound;
			else
				to = pos;

			String identOrKeyword;
			try {
				identOrKeyword = fDocument.get(from, to - from);
			} catch (BadLocationException e) {
				return TOKEN_EOF;
			}

			return getToken(identOrKeyword);

		}
		// operators, number literals etc
		return TOKEN_OTHER;

	}

	/**
	 * Returns the next token in backward direction, starting at <code>start</code>, and not extending further
	 * than <code>bound</code>. The return value is one of the constants defined in {@link Symbols}. After a
	 * call, {@link #getPosition()} will return the position just before the scanned token starts (i.e. the
	 * next position that will be scanned).
	 * 
	 * @param start
	 *            the first character position in the document to consider
	 * @param bound
	 *            the first position not to consider any more
	 * @return a constant from {@link Symbols} describing the previous token
	 */
	public int previousToken(int start, int bound) {
		int pos = scanBackward(start, bound, fNonWSDefaultPart);
		if (pos == NOT_FOUND)
			return TOKEN_EOF;

		fPos--;

		switch (fChar) {
			case LBRACE:
				return TOKEN_LBRACE;
			case RBRACE:
				return TOKEN_RBRACE;
			case LBRACKET:
				return TOKEN_LBRACKET;
			case RBRACKET:
				return TOKEN_RBRACKET;
			case LPAREN:
				return TOKEN_LPAREN;
			case RPAREN:
				return TOKEN_RPAREN;
			case SEMICOLON:
				return TOKEN_SEMICOLON;
			case COLON:
				return TOKEN_COLON;
			case COMMA:
				return TOKEN_COMMA;
			case QUESTIONMARK:
				return TOKEN_QUESTIONMARK;
			case EQUAL:
				return TOKEN_EQUAL;
		}

		// else
		if (Character.isJavaIdentifierPart(fChar)) {
			// assume an ident or keyword
			int from, to = pos + 1;
			pos = scanBackward(pos - 1, bound, fNonIdent);
			if (pos == NOT_FOUND)
				from = bound == UNBOUND ? 0 : bound + 1;
			else
				from = pos + 1;

			String identOrKeyword;
			try {
				identOrKeyword = fDocument.get(from, to - from);
			} catch (BadLocationException e) {
				return TOKEN_EOF;
			}

			return getToken(identOrKeyword);

		} else {
			// operators, number literals etc
			return TOKEN_OTHER;
		}

	}

	/**
	 * Finds the highest position in <code>fDocument</code> such that the position is &lt;=
	 * <code>position</code> and &gt; <code>bound</code> and <code>fDocument.getChar(position) == ch</code>
	 * evaluates to <code>true</code> for at least one ch in <code>chars</code> and the position is in the
	 * default partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &lt; <code>position</code>, or <code>UNBOUND</code>
	 * @param ch
	 *            the <code>char</code> to search for
	 * @return the highest position of one element in <code>chars</code> in (<code>bound</code>,
	 *         <code>position</code>] that resides in a Atl partition, or <code>NOT_FOUND</code> if none can
	 *         be found
	 */
	public int scanBackward(int position, int bound, char ch) {
		return scanBackward(position, bound, new CharacterMatch(ch));
	}

	/**
	 * Finds the highest position in <code>fDocument</code> such that the position is &lt;=
	 * <code>position</code> and &gt; <code>bound</code> and <code>fDocument.getChar(position) == ch</code>
	 * evaluates to <code>true</code> for at least one ch in <code>chars</code> and the position is in the
	 * default partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &lt; <code>position</code>, or <code>UNBOUND</code>
	 * @param chars
	 *            an array of <code>char</code> to search for
	 * @return the highest position of one element in <code>chars</code> in (<code>bound</code>,
	 *         <code>position</code>] that resides in a Atl partition, or <code>NOT_FOUND</code> if none can
	 *         be found
	 */
	public int scanBackward(int position, int bound, char[] chars) {
		return scanBackward(position, bound, new CharacterMatch(chars));
	}

	/**
	 * Finds the highest position <code>p</code> in <code>fDocument</code> such that <code>bound</code> &lt;
	 * <code>p</code> &lt;= <code>start</code> and <code>condition.stop(fDocument.getChar(p), p)</code>
	 * evaluates to <code>true</code>.
	 * 
	 * @param start
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &lt; <code>start</code>, or <code>UNBOUND</code>
	 * @param condition
	 *            the <code>StopCondition</code> to check
	 * @return the highest position in (<code>bound</code>, <code>start</code> for which
	 *         <code>condition</code> holds, or <code>NOT_FOUND</code> if none can be found
	 */
	public int scanBackward(int start, int bound, StopCondition condition) {
		if (bound == UNBOUND)
			bound = -1;

		Assert.isTrue(bound >= -1);
		Assert.isTrue(start < fDocument.getLength());

		try {
			fPos = start;
			while (fPos > bound) {

				fChar = fDocument.getChar(fPos);
				if (condition.stop(fChar, fPos, false))
					return fPos;

				fPos--;
			}
		} catch (BadLocationException e) {
		}
		return NOT_FOUND;
	}

	/**
	 * Finds the lowest position in <code>fDocument</code> such that the position is &gt;=
	 * <code>position</code> and &lt; <code>bound</code> and <code>fDocument.getChar(position) == ch</code>
	 * evaluates to <code>true</code> and the position is in the default partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &gt; <code>position</code>, or <code>UNBOUND</code>
	 * @param ch
	 *            the <code>char</code> to search for
	 * @return the lowest position of <code>ch</code> in (<code>bound</code>, <code>position</code>] that
	 *         resides in a Atl partition, or <code>NOT_FOUND</code> if none can be found
	 */
	public int scanForward(int position, int bound, char ch) {
		return scanForward(position, bound, new CharacterMatch(ch));
	}

	/**
	 * Finds the lowest position in <code>fDocument</code> such that the position is &gt;=
	 * <code>position</code> and &lt; <code>bound</code> and <code>fDocument.getChar(position) == ch</code>
	 * evaluates to <code>true</code> for at least one ch in <code>chars</code> and the position is in the
	 * default partition.
	 * 
	 * @param position
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &gt; <code>position</code>, or <code>UNBOUND</code>
	 * @param chars
	 *            an array of <code>char</code> to search for
	 * @return the lowest position of a non-whitespace character in [<code>position</code>, <code>bound</code>
	 *         ) that resides in a Atl partition, or <code>NOT_FOUND</code> if none can be found
	 */
	public int scanForward(int position, int bound, char[] chars) {
		return scanForward(position, bound, new CharacterMatch(chars));
	}

	/**
	 * Finds the lowest position <code>p</code> in <code>fDocument</code> such that <code>start</code> &lt;= p
	 * &lt; <code>bound</code> and <code>condition.stop(fDocument.getChar(p), p)</code> evaluates to
	 * <code>true</code>.
	 * 
	 * @param start
	 *            the first character position in <code>fDocument</code> to be considered
	 * @param bound
	 *            the first position in <code>fDocument</code> to not consider any more, with
	 *            <code>bound</code> &gt; <code>start</code>, or <code>UNBOUND</code>
	 * @param condition
	 *            the <code>StopCondition</code> to check
	 * @return the lowest position in [<code>start</code>, <code>bound</code>) for which
	 *         <code>condition</code> holds, or <code>NOT_FOUND</code> if none can be found
	 */
	public int scanForward(int start, int bound, StopCondition condition) {
		Assert.isTrue(start >= 0);

		if (bound == UNBOUND)
			bound = fDocument.getLength();

		Assert.isTrue(bound <= fDocument.getLength());

		try {
			fPos = start;
			while (fPos < bound) {

				fChar = fDocument.getChar(fPos);
				if (condition.stop(fChar, fPos, true))
					return fPos;

				fPos++;
			}
		} catch (BadLocationException e) {
		}
		return NOT_FOUND;
	}

}
