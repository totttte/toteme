/**
 * �쐬��: 2006/08/24
 */
package tottemsn.core;

import java.io.UnsupportedEncodingException;


/**
 * <p>MSNP11�����������n�߂�XML�ŏ����ꂽPayload�������܂ރR�}���h�Ɋւ��鏈�����s���܂��B</p>
 * <ul>
 * ��Ɉȉ��̃R�}���h�Ɋւ��鏈���������܂��B
 *  <li>UUX�E�E�E�������p�[�\�i�����b�Z�[�W��ݒ肷�邽�߂̃R�}���h</li>
 *  <li>UBX�E�E�E�t�����h���X�g�ɂ���N�����p�[�\�i�����b�Z�[�W��ݒ肵���Ƃ��̂��߂̃R�}���h</li>
 *  <li>GCF�E�E�E�E�B���N�A�J�����A�w�i�̋��L�Ȃǂ������R�}���h�B</li>
 * </ul>
 * <p>�����̃R�}���h�ɂ͈ȉ��̂悤�Ȍ`���ɂȂ��Ă��܂��B</p>
 * UUX�����GCF
 * <pre>
 * UUX //TrID //payloadsize(integer)\r\n
 * //XMLDocument
 * </pre>
 * UBX
 * <pre>
 * UBX account //payloadsize(integer)\r\n
 * //XMLDocument
 * </pre>
 * <p>
 * Payload��t�����Ă���R�}���h�ɂ͑��ɂ��AMSG�R�}���h������܂����A�������HTML�̃w�b�_���\�b�h��
 * �߂��`���ŏ�����Ă���̂ŁA�N���X�������܂����B
 * </p>
 * <i>���̃N���X�̃C���X�^���X�́A�p�x�ɌĂяo���ꐶ�������\���������A���ꂼ���XML�p�[�T�̏����́A
 * ��r�I�ȒP�Ȃ��̂������̂ŁAjavax.xml.*�p�b�P�[�W�̃p�[�T�͎g���Ă��܂���B
 * </i>
 * @see tottemsn.core.StringUtil#isfragmentation(String)
 */
class XMLPayload {

    private String address;
    private String psm;
    
    /**
     * <p>��M�����p�P�b�g����UBX�R�}���h�̉�͗p�C���X�^���X�𐶐����܂��B</p>
     * <i>�Ȃ��A���̕����̃R�}���h�Ɠ����Ɏ�M�����Ƃ��̂��߂ɁA���炩����
     * {@link StringUtil#getMSG(String, int)}�ł��̃R�}���h�݂̂����o���Ă����K�v������܂��B
     * �܂��A���̃R�}���h���f�Љ����Ă��Ȃ����Ƃ��K�v�����ł��B</i>
     * @param ubx ubx�R�}���h��UBX�R�}���h�ɑ���payload�B
     */
    XMLPayload(String ubx) {
        this.address = StringUtil.messeToken(ubx,1);
        if(ubx.indexOf('\n') >= ubx.length() - 1)
            this.psm = "";
        else {
            String xml = ubx.substring(ubx.indexOf('\n')+1);
            this.psm = getAttribute(xml,"PSM");
        }
    }
    
    String getAddress() {
        return address;
    }
    
    String getPersonalMessage() {
        return psm;
    }
    
    public String toString() {
        return  "[account=" + address + ",psm=" + psm + "]";
    }
    
    /**
     * UUX�R�}���h�`���̕���������܂��B
     * @param psm personal Message���w�肵�܂��B
     * @return xml�`���ɂȂ���UUX�R�}���h
     */
    static String makeUUXCommand(String psm) {
        final String header = "<Data><PSM>";
        final String footer = "</PSM><CurrentMedia></CurrentMedia></Data>";
        String payload = header + psm + footer;
        int length = 0;
        try {
            length = payload.getBytes("UTF-8").length;
        } catch(UnsupportedEncodingException e) {
        }
        return "UUX //TrID " + length + "\r\n" + payload;
    }
    
    /** <p>XML�̗v�f������A���̗v�f�����o���܂��B</p>
     * ��j
     * <pre>
     * String src = "&lt;data&gt;&lt;psm&gt;test&lt;/psm&gt;&lt;/data&gt;";
     * String name = "psm";
     * System.out.println(  getAttribute(src,name) );
     * >test
     * </pre>
     * @param src XML�h�L�������g
     * @param name ���ׂ����v�f
     */
    private static String getAttribute(String src,String name) {
        int begin = src.indexOf(name) + name.length() + 1;
        int end = src.indexOf(name,begin) - 2;
        return src.substring(begin,end);
    }
}