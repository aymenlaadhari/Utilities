/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

/**
 *
 * @author aladhari
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class JTextFieldAutoCompletion implements DocumentListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4810213451949301347L;

	//Les Données De L'AutoCompletion
	private List<String> data = new ArrayList<>();
        private List<String> dataTemp;
        private final JTextField textField;
	
	/**
	 * Constructeur Paramétré à deux arguments
     * @param jTextFieldLocal
	 * @param data les données de l'autocompletion
	 */
	public JTextFieldAutoCompletion(JTextField jTextFieldLocal, List<String> data) {
		textField = jTextFieldLocal;
		//ici on fait appel à la méthode setDataCompletion pour definir les données de l'autocompletion
		this.setDataCompletion(data);
		//je défini l'ecouteur de l'evenement de la saisie
		textField.getDocument().addDocumentListener(this);
		//je défini j'ecouteur de la touche entrer
		textField.addActionListener(this);
                
	}
	/**
	 * Permet De Redefinir les données de l'autocompletion
	 * @param data les données de l'autocompletion
	 */
	public void setDataCompletion(List<String> data) {
		//on affecte seulement si data est déffirent à null
		if(data != null)
			this.data = data;
                dataTemp = new ArrayList<>();
                this.data.stream().forEach((cnsmr)->{
                  dataTemp.add(cnsmr.toLowerCase());
                });
		//on va trier les données de l'autocompletion 
                Collections.sort(this.data);
	}
	/**
	 * Evenement Déclenché à chaque fois que l'utilisateur tape un caractère quelconque, ou fasse une copier/coller dans le champs de texte.
     * @param e
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Stub de la méthode généré automatiquement
		//on arréte l'exécution de l'evenement si l'utilisateur fasse une copier/coller
		if(e.getLength() != 1) return;
		
		//on récupére la position du dernier caratère saisie en comptant de zéro, premier caractère est en position 0, le deuxième à 2 etc..
		int pos = e.getOffset();
		String prefix = null;
		try {
			//on recupére dans prefix ce qu'a saisi l'utilisateur jusqu'à présent.
			prefix = textField.getText(0, pos + 1);
                        prefix = prefix.toLowerCase();
                 
		} catch (BadLocationException e1) {}
                
		//on fait une recherche sur la chaine qu'a saisi l'utilisateur dans les données de l'autocompletion. 
		//la méthode binarySearch retourne :
		//Soit l'index de l'element cherché s'il est contenu dans la collection.
		//Soit le nombre d'element de la collection si tous les elements sont inférieurs à l'element qu'on cherche.
		//Soit un entier négatif qui représente l'index de premier element supérieur de l'element qu'on cherche.
                
		int index = Collections.binarySearch(dataTemp, prefix);
		if(index < 0 && -index <= dataTemp.size()) {
			//Completion Trouvé
			//On récupére le premier element supérieur à l'element cherché. le signe - retourne la valeur absolue de la variable index. 
			String match = dataTemp.get(-index - 1).toLowerCase();
			String realMatch = data.get(-index - 1); 
                       
			//on s'assure que la chaine dans la variable match commence par la chaine contenu dans la variable prefix c-à-d ce qu'a saisi l'utilisateur 
			if(match.startsWith(prefix)) {
				//si oui on met on place l'autocompletion sinon on fait rien :).
                                
				SwingUtilities.invokeLater(new AutoCompletion(pos, realMatch));
			}
		} else ;
			//Aucune Completion Trouvé
		
	}
	/**
	 * Permet De Valider L'AutoCompletion En Cliquant Sur La Touche Entrer
     * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Stub de la méthode généré automatiquement
		textField.setCaretPosition(textField.getSelectionEnd());
	}
	@Override
	public void removeUpdate(DocumentEvent e) {}
	@Override
	public void changedUpdate(DocumentEvent e) {}
	
	   private class AutoCompletion implements Runnable {

        private final int pos;
        private final String completion;

        public AutoCompletion(int pos, String completion) {
            this.pos = pos;

            this.completion = completion;

        }

        @Override
        public void run() {
            // TODO Stub de la méthode généré automatiquement
            //On affecte la chaine trouvé pour l'autocompletion dans le champs de texte
            textField.setText(completion);
            //on definit à partir d'où va débuter la séléction des caractères ajouté comme completion. 
            //j'ai précisé qu'il va débuter de la fin vers le dernier caractère sasie par l'utilisateur
            textField.setCaretPosition(completion.length());
            //j'ai appliqué la séléction jusqu'au dernier caractère sasie par l'utilisateur
            textField.moveCaretPosition(pos + 1);
        }
    }
}
