import java.util.ArrayList;
import java.util.Collections;

class DeckFactory
{
	public static Deck<Card> createStandardDeck()
	{
		Card[] cards = new Card[52];
		int pos = 0;
		
		for(int suit = 1; suit < Card.Suit.values().length; suit++)
			for(int rank = 1; rank < Card.Rank.values().length; rank++)
				cards[pos++] = new Card(Card.Rank.values()[rank], Card.Suit.values()[suit]);
		
		return new Deck<>(cards);
	}
}

public class Deck<C extends Card>
{
	private ArrayList<C> cardDeck = new ArrayList<>();
	
	public Deck() {}
	
	public Deck(C[] cards)
	{
		for(C card : cards)
			cardDeck.add(card);
	}
	
	public void shuffle()
	{
		Collections.shuffle(cardDeck);
	}
	
	public void reverse()
	{
		Collections.reverse(cardDeck);
	}
	
	public void sort()
	{
		Collections.sort(cardDeck);
	}
	
	public void flipAll()
	{
		for(C card : cardDeck)
			card.flip();
	}
	
	public void addBottom(C card)
	{
		cardDeck.add(card);
	}
	
	public void addBottom(C[] cards)
	{
		for(C card : cards)
			addBottom(card);
	}
	
	public void addBottom(Deck<C> deck)
	{
		for(C card : deck.cardDeck)
			addBottom(card);
	}
	
	public void addTop(C card)
	{
		cardDeck.add(0, card);
	}
	
	public void addTop(C[] cards)
	{
		ArrayList<C> temp = new ArrayList<C>();
		for(C card : cards)
			temp.add(card);
		
		getReversedList(temp).forEach((C card) -> {addTop(card);});
	}
	
	private ArrayList<C> getReversedList(ArrayList<C> list)
	{
		ArrayList<C> temp = new ArrayList<>(list);
		
		Collections.reverse(temp);
		
		return temp;
	}

	public void addTop(Deck<C> deck)
	{
		getReversedList(deck.cardDeck).forEach((C card) -> {addTop(card);});	
	}
	
	public C peak()
	{
		return cardDeck.get(0);
	}
	
	public C pop()
	{
		return cardDeck.remove(0);
	}
	
	public C element()
	{
		return cardDeck.get(cardDeck.size() - 1);
	}
	
	public C poll()
	{
		return cardDeck.remove(cardDeck.size() - 1);
	}
}
