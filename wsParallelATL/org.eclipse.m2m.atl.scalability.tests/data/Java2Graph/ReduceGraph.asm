<?xml version = '1.0' encoding = 'ISO-8859-1' ?>
<asm version="1.0" name="0">
	<cp>
		<constant value="ReduceGraph"/>
		<constant value="links"/>
		<constant value="NTransientLinkSet;"/>
		<constant value="col"/>
		<constant value="J"/>
		<constant value="main"/>
		<constant value="A"/>
		<constant value="OclParametrizedType"/>
		<constant value="#native"/>
		<constant value="Collection"/>
		<constant value="J.setName(S):V"/>
		<constant value="OclSimpleType"/>
		<constant value="OclAny"/>
		<constant value="J.setElementType(J):V"/>
		<constant value="TransientLinkSet"/>
		<constant value="A.__matcher__():V"/>
		<constant value="A.__exec__():V"/>
		<constant value="self"/>
		<constant value="__resolve__"/>
		<constant value="1"/>
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
		<constant value="A.__matchnode():V"/>
		<constant value="A.__matchedge():V"/>
		<constant value="__exec__"/>
		<constant value="node"/>
		<constant value="NTransientLinkSet;.getLinksByRule(S):QNTransientLink;"/>
		<constant value="A.__applynode(NTransientLink;):V"/>
		<constant value="edge"/>
		<constant value="A.__applyedge(NTransientLink;):V"/>
		<constant value="__matchnode"/>
		<constant value="Node"/>
		<constant value="MM"/>
		<constant value="IN"/>
		<constant value="MMOF!Classifier;.allInstancesFrom(S):QJ"/>
		<constant value="size"/>
		<constant value="5"/>
		<constant value="J.&gt;(J):J"/>
		<constant value="B.not():B"/>
		<constant value="33"/>
		<constant value="TransientLink"/>
		<constant value="NTransientLink;.setRule(MATL!Rule;):V"/>
		<constant value="n"/>
		<constant value="NTransientLink;.addSourceElement(SJ):V"/>
		<constant value="n1"/>
		<constant value="MM1"/>
		<constant value="NTransientLink;.addTargetElement(SJ):V"/>
		<constant value="NTransientLinkSet;.addLink2(NTransientLink;B):V"/>
		<constant value="10:16-10:17"/>
		<constant value="10:16-10:22"/>
		<constant value="10:23-10:24"/>
		<constant value="10:16-10:24"/>
		<constant value="12:3-16:4"/>
		<constant value="__applynode"/>
		<constant value="NTransientLink;"/>
		<constant value="NTransientLink;.getSourceElement(S):J"/>
		<constant value="NTransientLink;.getTargetElement(S):J"/>
		<constant value="3"/>
		<constant value="type"/>
		<constant value="13:12-13:13"/>
		<constant value="13:12-13:18"/>
		<constant value="13:4-13:18"/>
		<constant value="14:12-14:13"/>
		<constant value="14:12-14:18"/>
		<constant value="14:4-14:18"/>
		<constant value="15:12-15:13"/>
		<constant value="15:12-15:18"/>
		<constant value="15:4-15:18"/>
		<constant value="link"/>
		<constant value="__matchedge"/>
		<constant value="Edge"/>
		<constant value="source"/>
		<constant value="J.oclIsUndefined():J"/>
		<constant value="J.not():J"/>
		<constant value="target"/>
		<constant value="J.and(J):J"/>
		<constant value="19"/>
		<constant value="52"/>
		<constant value="e1"/>
		<constant value="21:23-21:24"/>
		<constant value="21:23-21:31"/>
		<constant value="21:23-21:48"/>
		<constant value="21:19-21:48"/>
		<constant value="21:57-21:58"/>
		<constant value="21:57-21:65"/>
		<constant value="21:57-21:82"/>
		<constant value="21:53-21:82"/>
		<constant value="21:19-21:82"/>
		<constant value="21:129-21:134"/>
		<constant value="21:88-21:89"/>
		<constant value="21:88-21:96"/>
		<constant value="21:88-21:101"/>
		<constant value="21:102-21:103"/>
		<constant value="21:88-21:103"/>
		<constant value="21:108-21:109"/>
		<constant value="21:108-21:116"/>
		<constant value="21:108-21:121"/>
		<constant value="21:122-21:123"/>
		<constant value="21:108-21:123"/>
		<constant value="21:88-21:123"/>
		<constant value="21:16-21:140"/>
		<constant value="23:3-26:4"/>
		<constant value="__applyedge"/>
		<constant value="24:14-24:15"/>
		<constant value="24:14-24:22"/>
		<constant value="24:4-24:22"/>
		<constant value="25:14-25:15"/>
		<constant value="25:14-25:22"/>
		<constant value="25:4-25:22"/>
	</cp>
	<field name="1" type="2"/>
	<field name="3" type="4"/>
	<operation name="5">
		<context type="6"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<push arg="7"/>
			<push arg="8"/>
			<new/>
			<dup/>
			<push arg="9"/>
			<pcall arg="10"/>
			<dup/>
			<push arg="11"/>
			<push arg="8"/>
			<new/>
			<dup/>
			<push arg="12"/>
			<pcall arg="10"/>
			<pcall arg="13"/>
			<set arg="3"/>
			<getasm/>
			<push arg="14"/>
			<push arg="8"/>
			<new/>
			<set arg="1"/>
			<getasm/>
			<pcall arg="15"/>
			<getasm/>
			<pcall arg="16"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="17" begin="0" end="24"/>
		</localvariabletable>
	</operation>
	<operation name="18">
		<context type="6"/>
		<parameters>
			<parameter name="19" type="4"/>
		</parameters>
		<code>
			<load arg="19"/>
			<getasm/>
			<get arg="3"/>
			<call arg="20"/>
			<if arg="21"/>
			<getasm/>
			<get arg="1"/>
			<load arg="19"/>
			<call arg="22"/>
			<dup/>
			<call arg="23"/>
			<if arg="24"/>
			<load arg="19"/>
			<call arg="25"/>
			<goto arg="26"/>
			<pop/>
			<load arg="19"/>
			<goto arg="27"/>
			<push arg="28"/>
			<push arg="8"/>
			<new/>
			<load arg="19"/>
			<iterate/>
			<store arg="29"/>
			<getasm/>
			<load arg="29"/>
			<call arg="30"/>
			<call arg="31"/>
			<enditerate/>
			<call arg="32"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="2" name="33" begin="23" end="27"/>
			<lve slot="0" name="17" begin="0" end="29"/>
			<lve slot="1" name="34" begin="0" end="29"/>
		</localvariabletable>
	</operation>
	<operation name="35">
		<context type="6"/>
		<parameters>
			<parameter name="19" type="4"/>
			<parameter name="29" type="36"/>
		</parameters>
		<code>
			<getasm/>
			<get arg="1"/>
			<load arg="19"/>
			<call arg="22"/>
			<load arg="19"/>
			<load arg="29"/>
			<call arg="37"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="17" begin="0" end="6"/>
			<lve slot="1" name="34" begin="0" end="6"/>
			<lve slot="2" name="38" begin="0" end="6"/>
		</localvariabletable>
	</operation>
	<operation name="39">
		<context type="6"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<pcall arg="40"/>
			<getasm/>
			<pcall arg="41"/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="0" name="17" begin="0" end="3"/>
		</localvariabletable>
	</operation>
	<operation name="42">
		<context type="6"/>
		<parameters>
		</parameters>
		<code>
			<getasm/>
			<get arg="1"/>
			<push arg="43"/>
			<call arg="44"/>
			<iterate/>
			<store arg="19"/>
			<getasm/>
			<load arg="19"/>
			<pcall arg="45"/>
			<enditerate/>
			<getasm/>
			<get arg="1"/>
			<push arg="46"/>
			<call arg="44"/>
			<iterate/>
			<store arg="19"/>
			<getasm/>
			<load arg="19"/>
			<pcall arg="47"/>
			<enditerate/>
		</code>
		<linenumbertable>
		</linenumbertable>
		<localvariabletable>
			<lve slot="1" name="33" begin="5" end="8"/>
			<lve slot="1" name="33" begin="15" end="18"/>
			<lve slot="0" name="17" begin="0" end="19"/>
		</localvariabletable>
	</operation>
	<operation name="48">
		<context type="6"/>
		<parameters>
		</parameters>
		<code>
			<push arg="49"/>
			<push arg="50"/>
			<findme/>
			<push arg="51"/>
			<call arg="52"/>
			<iterate/>
			<store arg="19"/>
			<load arg="19"/>
			<get arg="53"/>
			<pushi arg="54"/>
			<call arg="55"/>
			<call arg="56"/>
			<if arg="57"/>
			<getasm/>
			<get arg="1"/>
			<push arg="58"/>
			<push arg="8"/>
			<new/>
			<dup/>
			<push arg="43"/>
			<pcall arg="59"/>
			<dup/>
			<push arg="60"/>
			<load arg="19"/>
			<pcall arg="61"/>
			<dup/>
			<push arg="62"/>
			<push arg="49"/>
			<push arg="63"/>
			<new/>
			<pcall arg="64"/>
			<pusht/>
			<pcall arg="65"/>
			<enditerate/>
		</code>
		<linenumbertable>
			<lne id="66" begin="7" end="7"/>
			<lne id="67" begin="7" end="8"/>
			<lne id="68" begin="9" end="9"/>
			<lne id="69" begin="7" end="10"/>
			<lne id="70" begin="25" end="30"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="1" name="60" begin="6" end="32"/>
			<lve slot="0" name="17" begin="0" end="33"/>
		</localvariabletable>
	</operation>
	<operation name="71">
		<context type="6"/>
		<parameters>
			<parameter name="19" type="72"/>
		</parameters>
		<code>
			<load arg="19"/>
			<push arg="60"/>
			<call arg="73"/>
			<store arg="29"/>
			<load arg="19"/>
			<push arg="62"/>
			<call arg="74"/>
			<store arg="75"/>
			<load arg="75"/>
			<dup/>
			<getasm/>
			<load arg="29"/>
			<get arg="38"/>
			<call arg="30"/>
			<set arg="38"/>
			<dup/>
			<getasm/>
			<load arg="29"/>
			<get arg="53"/>
			<call arg="30"/>
			<set arg="53"/>
			<dup/>
			<getasm/>
			<load arg="29"/>
			<get arg="76"/>
			<call arg="30"/>
			<set arg="76"/>
			<pop/>
		</code>
		<linenumbertable>
			<lne id="77" begin="11" end="11"/>
			<lne id="78" begin="11" end="12"/>
			<lne id="79" begin="9" end="14"/>
			<lne id="80" begin="17" end="17"/>
			<lne id="81" begin="17" end="18"/>
			<lne id="82" begin="15" end="20"/>
			<lne id="83" begin="23" end="23"/>
			<lne id="84" begin="23" end="24"/>
			<lne id="85" begin="21" end="26"/>
			<lne id="70" begin="8" end="27"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="3" name="62" begin="7" end="27"/>
			<lve slot="2" name="60" begin="3" end="27"/>
			<lve slot="0" name="17" begin="0" end="27"/>
			<lve slot="1" name="86" begin="0" end="27"/>
		</localvariabletable>
	</operation>
	<operation name="87">
		<context type="6"/>
		<parameters>
		</parameters>
		<code>
			<push arg="88"/>
			<push arg="50"/>
			<findme/>
			<push arg="51"/>
			<call arg="52"/>
			<iterate/>
			<store arg="19"/>
			<load arg="19"/>
			<get arg="89"/>
			<call arg="90"/>
			<call arg="91"/>
			<load arg="19"/>
			<get arg="92"/>
			<call arg="90"/>
			<call arg="91"/>
			<call arg="93"/>
			<if arg="94"/>
			<pushf/>
			<goto arg="27"/>
			<load arg="19"/>
			<get arg="89"/>
			<get arg="53"/>
			<pushi arg="54"/>
			<call arg="55"/>
			<load arg="19"/>
			<get arg="92"/>
			<get arg="53"/>
			<pushi arg="54"/>
			<call arg="55"/>
			<call arg="93"/>
			<call arg="56"/>
			<if arg="95"/>
			<getasm/>
			<get arg="1"/>
			<push arg="58"/>
			<push arg="8"/>
			<new/>
			<dup/>
			<push arg="46"/>
			<pcall arg="59"/>
			<dup/>
			<push arg="33"/>
			<load arg="19"/>
			<pcall arg="61"/>
			<dup/>
			<push arg="96"/>
			<push arg="88"/>
			<push arg="63"/>
			<new/>
			<pcall arg="64"/>
			<pusht/>
			<pcall arg="65"/>
			<enditerate/>
		</code>
		<linenumbertable>
			<lne id="97" begin="7" end="7"/>
			<lne id="98" begin="7" end="8"/>
			<lne id="99" begin="7" end="9"/>
			<lne id="100" begin="7" end="10"/>
			<lne id="101" begin="11" end="11"/>
			<lne id="102" begin="11" end="12"/>
			<lne id="103" begin="11" end="13"/>
			<lne id="104" begin="11" end="14"/>
			<lne id="105" begin="7" end="15"/>
			<lne id="106" begin="17" end="17"/>
			<lne id="107" begin="19" end="19"/>
			<lne id="108" begin="19" end="20"/>
			<lne id="109" begin="19" end="21"/>
			<lne id="110" begin="22" end="22"/>
			<lne id="111" begin="19" end="23"/>
			<lne id="112" begin="24" end="24"/>
			<lne id="113" begin="24" end="25"/>
			<lne id="114" begin="24" end="26"/>
			<lne id="115" begin="27" end="27"/>
			<lne id="116" begin="24" end="28"/>
			<lne id="117" begin="19" end="29"/>
			<lne id="118" begin="7" end="29"/>
			<lne id="119" begin="44" end="49"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="1" name="33" begin="6" end="51"/>
			<lve slot="0" name="17" begin="0" end="52"/>
		</localvariabletable>
	</operation>
	<operation name="120">
		<context type="6"/>
		<parameters>
			<parameter name="19" type="72"/>
		</parameters>
		<code>
			<load arg="19"/>
			<push arg="33"/>
			<call arg="73"/>
			<store arg="29"/>
			<load arg="19"/>
			<push arg="96"/>
			<call arg="74"/>
			<store arg="75"/>
			<load arg="75"/>
			<dup/>
			<getasm/>
			<load arg="29"/>
			<get arg="89"/>
			<call arg="30"/>
			<set arg="89"/>
			<dup/>
			<getasm/>
			<load arg="29"/>
			<get arg="92"/>
			<call arg="30"/>
			<set arg="92"/>
			<pop/>
		</code>
		<linenumbertable>
			<lne id="121" begin="11" end="11"/>
			<lne id="122" begin="11" end="12"/>
			<lne id="123" begin="9" end="14"/>
			<lne id="124" begin="17" end="17"/>
			<lne id="125" begin="17" end="18"/>
			<lne id="126" begin="15" end="20"/>
			<lne id="119" begin="8" end="21"/>
		</linenumbertable>
		<localvariabletable>
			<lve slot="3" name="96" begin="7" end="21"/>
			<lve slot="2" name="33" begin="3" end="21"/>
			<lve slot="0" name="17" begin="0" end="21"/>
			<lve slot="1" name="86" begin="0" end="21"/>
		</localvariabletable>
	</operation>
</asm>
