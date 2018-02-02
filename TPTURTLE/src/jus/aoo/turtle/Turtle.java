package jus.aoo.turtle;


import static jus.aoo.geometrie.DrawingSpace.Repere.CENTRE;

import java.io.IOException;

import jus.aoo.geometrie.DrawingSpace;
import jus.aoo.geometrie.Point;
import jus.aoo.geometrie.Segment;
import jus.aoo.geometrie.TurtleImage;
import jus.aoo.geometrie.Vecteur;
import jus.util.assertion.*;

/**
 * Réalise les fonctions d'une tortue du modèle Logo
 * @author	P.Morat ou http://imag.fr/Philippe.Morat ...
 * @version	1.0
 * @invariant Cohérent : Math.abs(Cap().module()-1) < Vecteur.EPSILON
 * @motcle tortue, Logo, tracé, dessin
 * @see <a href="Turtle.java">Turtle</a>
 */
public class Turtle {
	protected DrawingSpace feuille; // l'espace de déplacement de la tortue
	protected static final String imageFile = "/jus/aoo/turtle/Turtle.gif"; //le nom de l'image matérialisant la tortue.
	protected TurtleImage image; //l'image de la tortue 
	protected boolean estLeve;  // état de la plume
	protected Point position;   // la position courante de la tortue
	protected Vecteur cap;      // direction courante de la tortue
	/**
	* le constructeur de tortue
	* @param feuille la surface explorée
	* @require fmtOk : feuille!=null
	*/
	public Turtle(DrawingSpace feuille) {
		this.feuille=feuille;
		try{
			position = new Point(0,0);
		}catch(Require e) {
		}
		cap = new Vecteur(Vecteur.UNITE);
		estLeve=true;

		/* On réalise des transformations sur le support de dessin pour avoir le zero au centre de l'espace
		 * l'orientation positive des ordonnïées vers le haut et le cap zéro  à droite.
		 * On maintient cependant la rotation dans le sens anti-horaire */ 
		feuille.setRepere(CENTRE);
		try{
			image = new TurtleImage(position,imageFile);
			image.show(true);
		}catch(IOException e){
			e.printStackTrace();
		}
		feuille.addPermanent(image);
	}
	/**	
	* Fait avancer la tortue de d pas
	* @param d la distance à parcourir
	* @require argumentValide : d>=0
	* @require destinationVisible : destinationVisible()
	* @ensure CapInchange : _cap().equals(cap())
	* @ensure DeplacementOk : new Vecteur(_position(),position()).module()-d < Vecteur.EPSILON
	* @ensure capOk : new Vecteur(_position(),position()).colineaire(cap())
	* @invariant : visible
	*/
	public void avancer(int d) {
		if(!(destinationVisible(d, cap))) throw new Require("destinationVisible");
		Point _position = new Point(position);
		Vecteur v = new Vecteur(cap); v.homothetie(d);
		position.translation(v);
		image.translation(v);
		if(!estLeve){
			feuille.add(new Segment(_position,position));
		}else feuille.repaint();
		
		_invariant();
	}
	/**	
	* Fait reculer la tortue de d pas
	* @param d la distance à parcourir
	* @require argumentValide : d>=0
	* @require destinationVisible : destinationVisible()
	* @ensure CapInchange : _cap().equals(cap())
	* @ensure DeplacementOk : new Vecteur(_position(),position()).module()-d < Vecteur.EPSILON
	* @ensure capOk : new Vecteur(_position(),position()).colineaire(cap())
	*/
	public void reculer(int d) {
		if(!(destinationVisible(d, cap.oppose() ))) throw new Require("destinationVisible");
		Point _position = new Point(position);
		Vecteur v = cap.oppose(); v.homothetie(d);
		position.translation(v);
		image.translation(v);
		if(!estLeve) {
			feuille.add(new Segment(_position,position));
		}else feuille.repaint();
	}
	/**	
	* Fait tourner la à droite d'un angle a
	* @param a l'angle de rotation en degré
	* @require argumentValide : a>=0
	* @ensure PositionInchange : new Vecteur(_position(),position()).module() < Vecteur.EPSILON
	*/
	public void droite(int a) {
		cap.rotation((double)-a);
		image.rotation(-a);
		feuille.repaint();
	}
	/**	
	* Fait tourner la tortue à gauche d'un angle a
	* @param a l'angle de rotation en degré
	* @require argumentValide : a>=0
	* @ensure PositionInchange : new Vecteur(_position(),position()).module() < Vecteur.EPSILON
	*/
	public void gauche(int a) {
		cap.rotation((double)a);
		image.rotation(a);
		feuille.repaint();
	}
	/**	
	* Leve la plume
	* @ensure PlumeLevée : estLeve()
	*/
	public void lever() {estLeve = true;}
	/**	
	* Baisse la plume
	* @ensure PlumeLevée : !estLeve()
	*/
	public void baisser() {estLeve = false;}
	/**	
	* Restitue la représentation textuelle de la tortue
	* @return	la chaine
	*/
	public String toString() {return "Tortue["+position+","+cap+"]";}	
	/**	
	* Restitue la position de la tortue
	* @return	le Point de référence de la tortue
	*/
	public  Point position() {return new Point(position);}	
	/**	
	* Restitue le cap de la tortue
	* @return	le Vecteur de référence de la tortue
	*/
	public  Vecteur cap()  {return new Vecteur(cap);}	
	/**	
	* Restitue l'état de la plume
	* @return	true si la plume est levée
	*/
	public  boolean estLeve() {return estLeve;}

	/**	
	* verifie la visiblité de la tortue dans la feuille
	* @invarient : visible : visible()
	* @return    : true visible , false non visible
	*/
	public boolean visible() {
		return position.abscisse()  < feuille.getWidth()/2 
			&& position.abscisse()  > - feuille.getWidth()/2
			&& position.ordonnee()  < feuille.getHeight()/2
			&& position.ordonnee()  > - feuille.getHeight()/2;
	}
	/**	
	* verifie la visiblité de la tortue dans la feuille
	* @invarient : destvisible : desvisible()
	* @return    : true visible , false non visible
	*/
	public boolean destinationVisible(int d, Vecteur _cap) {
		Point _pointFutur = new Point(position);
		Vecteur v = new Vecteur(_cap); v.homothetie(d);
		_pointFutur.translation(v);
		
		return _pointFutur.abscisse()  < feuille.getWidth()/2 
				&& _pointFutur.abscisse()  > - feuille.getWidth()/2
				&& _pointFutur.ordonnee()  < feuille.getHeight()/2
				&& _pointFutur.ordonnee()  > - feuille.getHeight()/2;
	}
	 private void _invariant() {
		 if(!visible()) throw new Invariant("visible");
	 }
}