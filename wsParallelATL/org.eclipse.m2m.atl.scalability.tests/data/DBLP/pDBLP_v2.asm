<?xml version = '1.0' encoding = 'ISO-8859-1' ?>
<asm version="1.0" name="0">
	<cp>
		<constant value="DBLPv2"/>
		<constant value="links"/>
		<constant value="NTransientLinkSet;"/>
		<constant value="col"/>
		<constant value="J"/>
		<constant value="numRules"/>
		<constant value="I"/>
		<constant value="countRules"/>
		<constant value="A"/>
		<constant value="1"/>
		<constant value="self"/>
		<constant value="main"/>
		<constant value="OclParametrizedType"/>
		<constant value="#native"/>
		<constant value="Collection"/>
		<constant value="J.setName(S):V"/>
		<constant value="OclSimpleType"/>
		<constant value="OclAny"/>
		<constant value="J.setElementType(J):V"/>
		<constant value="TransientLinkSet"/>
		<constant value="A.__exec__():V"/>
		<constant value="A.__matcher__():V"/>
		<constant value="__resolve__"/>
		<constant value="J.oclIsKindOf(J):B"/>
		<constant value="18"/>
		<constant value="NTransientLinkSet;.getLinkBySourceElement(S):QNTransientLink;"/>
		<constant value="J.oclIsUndefined():B"/>
		<constant value="15"/>
		<constant value="NTransientLink;.getTargetFromSource(J):J"/>
		<constant value="17"/>
		<constant value="30"/>
		<constant value="Sequence"/>
		<constant value="2"/>
		<constant value="A.__resolve__(J):J"/>
		<constant value="QJ.including(J):QJ"/>
		<constant value="QJ.flatten():QJ"/>
		<constant value="e"/>
		<constant value="value"/>
		<constant value="resolveTemp"/>
		<constant value="S"/>
		<constant value="NTransientLink;.getNamedTargetFromSource(JS):J"/>
		<constant value="name"/>
		<constant value="__matcher__"/>
		<constant value="A.__matchicmt():V"/>
		<constant value="__exec__"/>
		<constant value="A.stopThread():V"/>
		<constant value="A.__applyGroupicmt():V"/>
		<constant value="booktitle"/>
		<constant value="MMM!InProceedings;"/>
		<constant value="0"/>
		<constant value="bootitle"/>
		<constant value="8:62-8:66"/>
		<constant value="8:62-8:75"/>
		<constant value="__matchicmt"/>
		<constant value="Author"/>
		<constant value="MM"/>
		<constant value="IN"/>
		<constant value="MMOF!Classifier;.allInstancesFrom(S):QJ"/>
		<constant value="records"/>
		<constant value="InProceedings"/>
		<constant value="J.oclIsTypeOf(J):J"/>
		<constant value="B.not():B"/>
		<constant value="24"/>
		<constant value="CJ.including(J):CJ"/>
		<constant value="J.booktitle():J"/>
		<constant value="ICMT"/>
		<constant value="J.=(J):J"/>
		<constant value="B.or(B):B"/>
		<constant value="55"/>
		<constant value="TransientLink"/>
		<constant value="icmt"/>
		<constant value="NTransientLink;.setRule(MATL!Rule;):V"/>
		<constant value="a"/>
		<constant value="NTransientLink;.addSourceElement(SJ):V"/>
		<constant value="out"/>
		<constant value="MM1"/>
		<constant value="NTransientLink;.addTargetElement(SJ):V"/>
		<constant value="NTransientLinkSet;.addLink2(NTransientLink;B):V"/>
		<constant value="A.decreaseCounter():V"/>
		<constant value="13:18-13:19"/>
		<constant value="13:18-13:27"/>
		<constant value="13:40-13:41"/>
		<constant value="13:54-13:70"/>
		<constant value="13:40-13:71"/>
		<constant value="13:18-13:72"/>
		<constant value="13:86-13:88"/>
		<constant value="13:86-13:100"/>
		<constant value="13:101-13:107"/>
		<constant value="13:86-13:107"/>
		<constant value="13:18-13:108"/>
		<constant value="15:3-18:4"/>
		<constant value="r"/>
		<constant value="ip"/>
		<constant value="__applyicmt"/>
		<constant value="NTransientLink;"/>
		<constant value="NTransientLink;.getSourceElement(S):J"/>
		<constant value="NTransientLink;.getTargetElement(S):J"/>
		<constant value="3"/>
		<constant value="4"/>
		<constant value="36"/>
		<constant value="47"/>
		<constant value="J.size():J"/>
		<constant value="numOfPapers"/>
		<constant value="16:12-16:13"/>
		<constant value="16:12-16:18"/>
		<constant value="16:4-16:18"/>
		<constant value="17:20-17:21"/>
		<constant value="17:20-17:29"/>
		<constant value="17:42-17:43"/>
		<constant value="17:56-17:72"/>
		<constant value="17:42-17:73"/>
		<constant value="17:20-17:74"/>
		<constant value="17:88-17:90"/>
		<constant value="17:88-17:102"/>
		<constant value="17:105-17:111"/>
		<constant value="17:88-17:111"/>
		<constant value="17:20-17:112"/>
		<constant value="17:20-17:120"/>
		<constant value="17:4-17:120"/>
		<constant value="link"/>
		<constant value="__applyGroupicmt"/>
		<constant value="NTransientLinkSet;.getLinksByRule(S):QNTransientLink;"/>
		<constant value="A.__applyicmt(NTransientLink;):V"/>
	</cp>
	<field name="1" type="2"/>
	<field name="3" type="4"/>
	<field name="5" type="6"/>
	<operation name="7">
		<context type="8"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<pushi arg="9"/>
			<set arg="5"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="10" begin="0" end="2"/>
		</localvariabletable>
	</operation>
	<operation name="11">
		<context type="8"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<push arg="12"/>
			<push arg="13"/>
			<new/>
			<dup/>
			<push arg="14"/>
			<pcall arg="15"/>
			<dup/>
			<push arg="16"/>
			<push arg="13"/>
			<new/>
			<dup/>
			<push arg="17"/>
			<pcall arg="15"/>
			<pcall arg="18"/>
			<set arg="3"/>
			<getasm/>
			<push arg="19"/>
			<push arg="13"/>
			<new/>
			<set arg="1"/>
			<getasm/>
			<tcall arg="20"/>
			<getasm/>
			<pcall arg="21"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="10" begin="0" end="24"/>
		</localvariabletable>
	</operation>
	<operation name="22">
		<context type="8"/>
		<parameters>
			<parameter name="9" type="4"/>
		</parameters>
		<code>
			<load arg="9"/>
			<getasm/>
			<get arg="3"/>
			<call arg="23"/>
			<if arg="24"/>
			<getasm/>
			<get arg="1"/>
			<load arg="9"/>
			<call arg="25"/>
			<dup/>
			<call arg="26"/>
			<if arg="27"/>
			<load arg="9"/>
			<call arg="28"/>
			<goto arg="29"/>
			<pop/>
			<load arg="9"/>
			<goto arg="30"/>
			<push arg="31"/>
			<push arg="13"/>
			<new/>
			<load arg="9"/>
			<iterate/>
			<store arg="32"/>
			<getasm/>
			<load arg="32"/>
			<call arg="33"/>
			<call arg="34"/>
			<enditerate/>
			<call arg="35"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="2" name="36" begin="23" end="27"/>
			<lve slot="0" name="10" begin="0" end="29"/>
			<lve slot="1" name="37" begin="0" end="29"/>
		</localvariabletable>
	</operation>
	<operation name="38">
		<context type="8"/>
		<parameters>
			<parameter name="9" type="4"/>
			<parameter name="32" type="39"/>
		</parameters>
		<code>
			<getasm/>
			<get arg="1"/>
			<load arg="9"/>
			<call arg="25"/>
			<load arg="9"/>
			<load arg="32"/>
			<call arg="40"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="10" begin="0" end="6"/>
			<lve slot="1" name="37" begin="0" end="6"/>
			<lve slot="2" name="41" begin="0" end="6"/>
		</localvariabletable>
	</operation>
	<operation name="42">
		<context type="8"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<tcall arg="43"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="10" begin="0" end="1"/>
		</localvariabletable>
	</operation>
	<operation name="44">
		<context type="8"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<call arg="45"/>
			<getasm/>
			<tcall arg="46"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="10" begin="0" end="3"/>
		</localvariabletable>
	</operation>
	<operation name="47">
		<context type="48"/>
		<parameters>
		</parameters>
		<code>
			<load arg="49"/>
			<get arg="50"/>
		</code>
		<linenumbertable>
			<lne id="51" begin="0" end="0"/>
			<lne id="52" begin="0" end="1"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="10" begin="0" end="1"/>
		</localvariabletable>
	</operation>
	<operation name="53">
		<context type="8"/>
		<parameters>
		</parameters>
		<code>
			<push arg="54"/>
			<push arg="55"/>
			<findme/>
			<push arg="56"/>
			<call arg="57"/>
			<iterate/>
			<store arg="9"/>
			<pushf/>
			<push arg="31"/>
			<push arg="13"/>
			<new/>
			<load arg="9"/>
			<get arg="58"/>
			<iterate/>
			<store arg="32"/>
			<load arg="32"/>
			<push arg="59"/>
			<push arg="55"/>
			<findme/>
			<call arg="60"/>
			<call arg="61"/>
			<if arg="62"/>
			<load arg="32"/>
			<call arg="63"/>
			<enditerate/>
			<iterate/>
			<store arg="32"/>
			<load arg="32"/>
			<call arg="64"/>
			<push arg="65"/>
			<call arg="66"/>
			<call arg="67"/>
			<enditerate/>
			<call arg="61"/>
			<if arg="68"/>
			<getasm/>
			<get arg="1"/>
			<push arg="69"/>
			<push arg="13"/>
			<new/>
			<dup/>
			<push arg="70"/>
			<pcall arg="71"/>
			<dup/>
			<push arg="72"/>
			<load arg="9"/>
			<pcall arg="73"/>
			<dup/>
			<push arg="74"/>
			<push arg="54"/>
			<push arg="75"/>
			<new/>
			<pcall arg="76"/>
			<pusht/>
			<pcall arg="77"/>
			<enditerate/>
			<getasm/>
			<call arg="78"/>
		</code>
		<linenumbertable>
			<lne id="79" begin="11" end="11"/>
			<lne id="80" begin="11" end="12"/>
			<lne id="81" begin="15" end="15"/>
			<lne id="82" begin="16" end="18"/>
			<lne id="83" begin="15" end="19"/>
			<lne id="84" begin="8" end="24"/>
			<lne id="85" begin="27" end="27"/>
			<lne id="86" begin="27" end="28"/>
			<lne id="87" begin="29" end="29"/>
			<lne id="88" begin="27" end="30"/>
			<lne id="89" begin="7" end="32"/>
			<lne id="90" begin="47" end="52"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="2" name="91" begin="14" end="23"/>
			<lve slot="2" name="92" begin="26" end="31"/>
			<lve slot="1" name="72" begin="6" end="54"/>
			<lve slot="0" name="10" begin="0" end="57"/>
		</localvariabletable>
	</operation>
	<operation name="93">
		<context type="8"/>
		<parameters>
			<parameter name="9" type="94"/>
		</parameters>
		<code>
			<load arg="9"/>
			<push arg="72"/>
			<call arg="95"/>
			<store arg="32"/>
			<load arg="9"/>
			<push arg="74"/>
			<call arg="96"/>
			<store arg="97"/>
			<load arg="97"/>
			<dup/>
			<getasm/>
			<load arg="32"/>
			<get arg="41"/>
			<call arg="33"/>
			<set arg="41"/>
			<dup/>
			<getasm/>
			<push arg="31"/>
			<push arg="13"/>
			<new/>
			<push arg="31"/>
			<push arg="13"/>
			<new/>
			<load arg="32"/>
			<get arg="58"/>
			<iterate/>
			<store arg="98"/>
			<load arg="98"/>
			<push arg="59"/>
			<push arg="55"/>
			<findme/>
			<call arg="60"/>
			<call arg="61"/>
			<if arg="99"/>
			<load arg="98"/>
			<call arg="63"/>
			<enditerate/>
			<iterate/>
			<store arg="98"/>
			<load arg="98"/>
			<call arg="64"/>
			<push arg="65"/>
			<call arg="66"/>
			<call arg="61"/>
			<if arg="100"/>
			<load arg="98"/>
			<call arg="63"/>
			<enditerate/>
			<call arg="101"/>
			<call arg="33"/>
			<set arg="102"/>
			<pop/>
		</code>
		<linenumbertable>
			<lne id="103" begin="11" end="11"/>
			<lne id="104" begin="11" end="12"/>
			<lne id="105" begin="9" end="14"/>
			<lne id="106" begin="23" end="23"/>
			<lne id="107" begin="23" end="24"/>
			<lne id="108" begin="27" end="27"/>
			<lne id="109" begin="28" end="30"/>
			<lne id="110" begin="27" end="31"/>
			<lne id="111" begin="20" end="36"/>
			<lne id="112" begin="39" end="39"/>
			<lne id="113" begin="39" end="40"/>
			<lne id="114" begin="41" end="41"/>
			<lne id="115" begin="39" end="42"/>
			<lne id="116" begin="17" end="47"/>
			<lne id="117" begin="17" end="48"/>
			<lne id="118" begin="15" end="50"/>
			<lne id="90" begin="8" end="51"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="4" name="91" begin="26" end="35"/>
			<lve slot="4" name="92" begin="38" end="46"/>
			<lve slot="3" name="74" begin="7" end="51"/>
			<lve slot="2" name="72" begin="3" end="51"/>
			<lve slot="0" name="10" begin="0" end="51"/>
			<lve slot="1" name="119" begin="0" end="51"/>
		</localvariabletable>
	</operation>
	<operation name="120">
		<context type="8"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<get arg="1"/>
			<push arg="70"/>
			<call arg="121"/>
			<iterate/>
			<store arg="9"/>
			<getasm/>
			<load arg="9"/>
			<pcall arg="122"/>
			<enditerate/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="1" name="36" begin="5" end="8"/>
			<lve slot="0" name="10" begin="0" end="9"/>
		</localvariabletable>
	</operation>
</asm>
